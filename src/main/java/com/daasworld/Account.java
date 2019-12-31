package com.daasworld;

import java.util.ArrayList;
import java.util.List;

public class Account {

    List<AccountEntry> history = new ArrayList<>();

    public Account() {
        // nothing
    }

    public Account( TransactionManager pool, long initialBalance ){
        Transaction t = pool.next();
        update(t, initialBalance);
        pool.end(t);
    }

    public synchronized void update(Transaction t, long amount) {
        history.add( new AccountEntry(t, amount));
    }

    public synchronized long balance(Transaction t) {

        // Visibility Rules ...

        // Ignore the transactions in the history where ...
        //      (a) txn id is greater than the current transaction t
        //      (b) txn id is greater than the maximum committed transaction when this txn was created
        //      (c) txn id is in the list of active transactions when this txn was created
        // and then take the most recent one ...

        long tid = t.id();
        long maxtid = t.max();
        List<Long> active = t.active();
        AccountEntry entry = history.stream()
                .filter((as) -> as.t.id() < tid)  // condition (a)
                .filter((as) -> as.t.id() <= maxtid) // condition (b)
                .filter((as) -> !active.contains(as.t.id()))
                .reduce( (as1, as2) -> as2) // this gets us the last element
                .orElse(new AccountEntry(null, 0)); // should never happen
        
        return entry.balance;
    }

    // for debugging ..
    public synchronized List<AccountEntry> history() {
        return new ArrayList<>(history);
    }
}
