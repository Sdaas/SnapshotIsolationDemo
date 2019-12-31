package com.daasworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Bank {
    private TransactionManager pool;
    private int numberOfAccounts;
    private List<Account> accounts = new ArrayList<>();

    public Bank(int numberOfAccounts, long startingBalance, TransactionManager pool) {
        this.pool = pool;
        this.numberOfAccounts = numberOfAccounts;
        for (int i = 0; i < numberOfAccounts; i++) {
            accounts.add(new Account(pool, startingBalance));
        }
    }

    public Bank(int numberOfAccounts, long startingBalance) {
        this(numberOfAccounts, startingBalance, new TransactionManager());
    }

    // Returns the sum of all balances in all accounts in the bank
    public long holdings() {
        Transaction txn = pool.next();
        long holding =  accounts.stream()
                .mapToLong( a -> a.balance(txn))
                .sum();
        pool.end(txn);
        return holding;
    }

    // This is a really slow running transaction to demo snapshot isolation
    public void longRunningRead(){
        Transaction txn = pool.next();
        long holding = 0L;

        for (Account a : accounts) {
            holding += a.balance(txn);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pool.end(txn);

        System.out.println("Long Reader. Holding: " + holding + " currentTxn: " + txn.id() + " maxTxn: " + pool.max());
    }

    public void doRandomTransfers(int numberOfTransfers, Random rand){
        // do transfers of random amounts of money between two random accounts.
        for (int i = 0; i < numberOfTransfers; i++) {
            int from = rand.nextInt(numberOfAccounts);
            int to = rand.nextInt(numberOfAccounts);

            int amount = rand.nextInt(100);
            transfer(from, to, amount);

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void transfer(int from, int to, long amount) {
        this.transfer(from, to, amount, true);
    }

    public void transfer(int from, int to, long amount, boolean endTransaction) {

        // to keep things simple don't allow self-transfers ...
        // here is why .. suppose account is
        // Account State txn = 10, balance = 1000;
        // Now txn 100 attempts to debit 100 from this. It sees last committed value as 1000.
        // So sets account state to txn = 100, balance = 900;
        // Now the same txn attempts to credit 100. But the account entry for txn=100 is invisible (because
        // this is Not ended yet). So it ends up seeing again txn=10 and balance = 1000

        if( from == to) return;

        Account fromAccount = accounts.get(from);
        Account toAccount = accounts.get(to);

        //We always obtain lock on the account with smaller number in order to avoid deadlock. If we do not do this.
        //then here can be a situation where Thread1 grabs a lock on X and then Y. and Thread2 attempts to do in in
        // reverse order leading to a deadlock.

        Account a1, a2;
        if( from < to ) {
            a1 = fromAccount;
            a2 = toAccount;
        }
        else {
            a1 = toAccount;
            a2 = fromAccount;
        }

        synchronized (a1) {
            synchronized (a2) {
                Transaction txn = pool.next();
                toAccount.update( txn, toAccount.balance(txn) + amount);
                fromAccount.update( txn, fromAccount.balance(txn) - amount);
                if( endTransaction ) pool.end(txn);
            }
        }

    }

    // for debugging
    public List<Long> active() {
        return pool.active();
    }

    // for debugging
    public Account account( int index) {
        return accounts.get(index);
    }
}
