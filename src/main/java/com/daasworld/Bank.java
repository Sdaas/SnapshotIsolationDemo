package com.daasworld;

import java.util.ArrayList;
import java.util.List;

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

    private boolean canUpdate(Account a, Transaction t) {
        // An account can be updated only if it has not been updated by
        // (a) any transaction greater than t
        // (b) any transaction that was in flight when t was created
        return a.canBeUpdatedBy(t);
    }

    public void transfer(int from, int to, long amount) {

        // to keep things simple don't allow self-transfers. Here is why ...
        // suppose account is
        // Account State txn = 10, balance = 1000;
        // Now txn 100 attempts to debit 100 from this. It sees last committed value as 1000.
        // So sets account state to txn = 100, balance = 900;
        // Now the same txn attempts to credit 100. But the account entry for txn=100 is invisible (because
        // this is Not ended yet). So it ends up seeing again txn=10 and balance = 1000

        //TODO Add support for same account transfer
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

        // Remember that we are working with a "snapshot" of the data. But the underlying data could have changed
        // since the snapshot was taken. So we need to check and handle that condition ....
        Transaction txn = pool.next();
        synchronized (a1) {
            synchronized (a2) {
                if( canUpdate(fromAccount, txn) && canUpdate(toAccount, txn)) {
                    toAccount.update( txn, toAccount.balance(txn) + amount);
                    fromAccount.update( txn, fromAccount.balance(txn) - amount);
                }
                else {
                    // Account(s) cannot be updated. So abort !
                    // Of course in real life we will want to do something different ....
                }
            }
        }
        pool.end(txn);
    }

    public int numberOfAccounts(){
        return numberOfAccounts;
    }

    // for debugging
    public Account account( int index) {
        return accounts.get(index);
    }
}
