## Overview

This application shows a simple implementation of Snapshot Isolation.

From [wikipedia](https://en.wikipedia.org/wiki/Snapshot_isolation) - "snapshot isolation is a guarantee that all reads made in a transaction will see a consistent snapshot of the database 
(in practice it reads the last committed values that existed at the time it started), and the transaction itself will successfully commit only if no updates it has made conflict with any concurrent updates made since that snapshot."


## Important Files

* `Transaction` : Keep track of maximum committed transaction and all transactions that were in flight when this was created.
* `TransactionManager` : generates new transactions, and keeps track of transactions in flight.
* `Account` : A record in the data store. Keeps a versioned history of its balances.
* `AccountEntry` : A single entry in an Account's history.
* `Bank` : Contains a collection of accounts and provides methods to transfer money.
* `BankDemo` : Illustrates the effect of snapshot isolation.

## Instructions

//TODO

## References

* Wikipedia article on [Snapshot Isolation](https://en.wikipedia.org/wiki/Snapshot_isolation)
* [Markdown syntax](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)
