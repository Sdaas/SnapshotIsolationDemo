package com.daasworld;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    //TODO Maybe we should be storing Transaction and List<Transaction> instead of implementation
    private long id;
    private long max; // the maximum transaction that was committed when this was created
    private List<Long> active; // the transactions that were active when this txn was created

    public Transaction(long id, List<Long> active, long max) {
        this.id = id;
        this.active = active;
        this.max = max;
    }

    public long id() {
        return id;
    }
    public long max() { return max;}

    // get list of transactions that were active when this transaction was created
    public List<Long> active() {
        // return a copy of the list;
        return new ArrayList<>(active);
    }
}

