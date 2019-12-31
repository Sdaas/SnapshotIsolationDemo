package com.daasworld;

import java.util.ArrayList;
import java.util.List;

// (a) Transactions are monotonically increasing
// (b) TM keeps track of all active transactions
// (c) TM keeps track of the _maximum_ transaction that has been committed ( note : this is not the same
// as the most _recent_ transaction_ that was committed 0

public class TransactionManager {
    private long next = 0; // the next transaction id that will be returned
    List<Long> active = new ArrayList<>(); // list of all active transactions
    private long maxCommitted = -1; // the maximum transaction that has been committed

    public synchronized Transaction next() {
        Transaction t = new Transaction(next, new ArrayList<>(active), maxCommitted);
        active.add(next);
        next++;
        return t;
    }

    // It is assumed here that "ending" a transaction is equivalent to a commit
    public synchronized void end (Transaction t) {
        long id = t.id();
        boolean success = active.remove(id);
        if (!success)  throw new IllegalArgumentException("Attempt to end a non-existent transaction");

        if( id > maxCommitted) maxCommitted = id;
    }

    // get list of all active transactions - used for debugging only
    public synchronized  List<Long> active() {
        // return a copy of the list.
        return new ArrayList<>(active);
    }

    // used for debugging only
    public long max() {
        return maxCommitted;
    }
}
