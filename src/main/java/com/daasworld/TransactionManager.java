package com.daasworld;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private long next = 0;
    List<Long> active = new ArrayList<>();

    public synchronized Transaction next() {
        Transaction t = new Transaction(next, new ArrayList<>(active));
        active.add(next);
        next++;
        return t;
    }

    public synchronized void end (Transaction t) {
        long id = t.id();
        boolean success = active.remove(id);
        if (!success)  throw new IllegalArgumentException("Attempt to end a non-existent transaction");
    }

    // get list of all active transactions - used for debugging only
    public synchronized  List<Long> active() {
        // return a copy of the list.
        return new ArrayList<>(active);
    }
}
