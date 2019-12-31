package com.daasworld;

public class AccountEntry {
    public Transaction t;
    public long balance;

    public AccountEntry(Transaction t, long balance){
        this.t = t;             // the transaction that made this update
        this.balance = balance; // value of the update
    }
}
