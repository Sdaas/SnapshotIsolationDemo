package com.daasworld;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private long id;
    private List<Long> activeTransactions; // the transactions that were active when this txn was created

    public Transaction(long id, List<Long> active) {
        this.id = id;
        activeTransactions = active;
    }

    public long id() {
        return id;
    }

    // get list of transactions that were active when this transaction was created
    public List<Long> active() {
        // return a copy of the list;
        return new ArrayList<>(activeTransactions);
    }
}

