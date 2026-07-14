package com.thecodeexperience.WithoutObjectPoolDesignPattern;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // --- PROBLEM 1: the cost is paid over and over --------------------------------------
        //
        // Eight requests come in. Each one does the obvious thing: open a connection, use it,
        // close it. The connection is discarded the moment the request ends, so the NEXT request
        // pays the full 120ms handshake all over again.
        System.out.println("=== 8 requests, each opening its own connection ===");

        long start = System.currentTimeMillis();
        for (int i = 1; i <= 8; i++) {
            DatabaseConnection connection = new DatabaseConnection();   // ⚠ 120ms, every time
            connection.query("SELECT * FROM orders WHERE id = " + i);
            connection.close();                                          // ⚠ and thrown away
        }
        long elapsed = System.currentTimeMillis() - start;

        System.out.println();
        System.out.println("  connections created : " + DatabaseConnection.totalEverCreated() + " for 8 requests");
        System.out.println("  wall time           : " + elapsed + "ms");
        System.out.println("  ⚠ ~960ms of that was handshakes. The actual queries took ~80ms.");
        System.out.println("    Every request paid the setup cost of an object the previous");
        System.out.println("    request had already finished building — and then threw away.");

        // --- PROBLEM 2: nothing is bounding you ---------------------------------------------
        //
        // Traffic spikes. Ten requests arrive at once, and each one does what it was told to do:
        // open a connection. Nothing in this design limits how many that can be — so the code
        // sails straight past the database's hard cap.
        System.out.println();
        System.out.println("=== a traffic spike: 10 concurrent requests (server max is 5) ===");

        AtomicInteger failures = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(10);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (int i = 1; i <= 10; i++) {
            final int request = i;
            executor.submit(() -> {
                DatabaseConnection connection = null;
                try {
                    connection = new DatabaseConnection();
                    connection.query("SELECT * FROM users WHERE id = " + request);
                } catch (IllegalStateException e) {
                    failures.incrementAndGet();
                    System.out.println("    request-" + request + " ✗ " + e.getMessage());
                } finally {
                    if (connection != null) {
                        connection.close();
                    }
                    done.countDown();
                }
            });
        }
        done.await();
        executor.shutdown();

        System.out.println();
        System.out.println("  requests rejected : " + failures.get() + " of 10   ⚠");
        System.out.println();
        System.out.println("⚠ Two failures, one cause: NOBODY OWNS THE LIFECYCLE.");
        System.out.println("  Each caller creates and destroys its own connection, so:");
        System.out.println("    - the expensive setup is repaid on every single request, and");
        System.out.println("    - there is no one place that could ever say \"that's enough, wait your turn\".");
        System.out.println("  Under load this doesn't degrade. It fails outright.");
    }

}
