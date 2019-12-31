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

    // starts up a bunch of threads and does random transfers between bank accounts
    private void doMultithreadedRandomTransfers() throws InterruptedException {
        // Set up a bunch of threads to do account transfers ..
        int numberOfThreads = 10;
        List<Callable<Void>> callables = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            callables.add(() -> {
                System.out.println("Thread number " + Thread.currentThread().getId());
                for(int j=0; j<10000; j++) {
                    bank.doRandomTransfers(1000, new Random());
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
        demo.doMultithreadedRandomTransfers();

    }
}

