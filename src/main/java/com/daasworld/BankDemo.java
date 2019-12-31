package com.daasworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// demonstrate application of snapshot isolation. Even though the data is being modified all the time,
// a slow reader is still able to determine the correct holding across all accounts.
public class BankDemo {

    private Bank bank;

    public BankDemo(int numberOfAccounts, long startingBalance ) {
        bank = new Bank(numberOfAccounts,startingBalance);
    }

    // print out the holdings of the bank every second ...
    private void printHoldings() {
        Thread t = new Thread(() -> {
            System.out.println("Printer is running");
            for (int i = 0; i < 1000; i++) {
                System.out.println("Holdings across all accounts is : " + bank.holdings());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    // exercise the long running reader also ....
    private void doLongRunningRead() {
        Thread t = new Thread(() -> {
            System.out.println("Long running reader is running");
            for (int i = 0; i < 1000; i++) {
                bank.longRunningRead();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void doRandomTransfer(Random rand) {
        int numberOfAccounts = bank.numberOfAccounts();
        int from = rand.nextInt(numberOfAccounts);
        int to = rand.nextInt(numberOfAccounts);

        int amount = rand.nextInt(100);
        bank.transfer(from, to, amount);
    }

    // starts up a bunch of threads and does random transfers between bank accounts
    private void doMultithreadedRandomTransfers() throws InterruptedException {

        Random random = new Random();
        int numberOfThreads = 10;
        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            callables.add(() -> {
                System.out.println("Thread number " + Thread.currentThread().getId());
                for(long j=0; j<100000; j++) {
                    doRandomTransfer(random);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Thread number " + Thread.currentThread().getId() + " is finished");
                return null;
            });
        }

        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        service.invokeAll(callables);
    }

    public static void main(String[] args) throws InterruptedException {

        // Set up a Bank
        int numberOfAccounts = 100;
        long startingBalance = 0;

        BankDemo demo = new BankDemo(numberOfAccounts, startingBalance);
        demo.printHoldings();
        demo.doLongRunningRead();
        demo.doMultithreadedRandomTransfers();
    }
}
