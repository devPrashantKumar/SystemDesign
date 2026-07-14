package com.thecodeexperience.WithObjectPoolDesignPattern;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        try (ConnectionPool pool = new ConnectionPool(3, 5000)) {

            // --- FIXED 1: the cost is paid once, not once per request ----------------------
            System.out.println("=== the same 8 requests, through a pool of 3 ===");

            long start = System.currentTimeMillis();
            for (int i = 1; i <= 8; i++) {
                DatabaseConnection connection = pool.acquire();
                try {
                    connection.query("SELECT * FROM orders WHERE id = " + i);
                } finally {
                    pool.release(connection);      // ⚠ ALWAYS in a finally. See the note below.
                }
            }
            long elapsed = System.currentTimeMillis() - start;

            System.out.println();
            System.out.println("  connections created : " + pool.created() + " for 8 requests   ✅ (was 8)");
            System.out.println("  served off the shelf: " + pool.reused() + " × 0ms instead of 120ms");
            System.out.println("  wall time           : " + elapsed + "ms   ✅ (was ~1040ms)");
            System.out.println("  ✅ The pool didn't make creation cheaper. It made it RARER.");

            // --- FIXED 2: the spike queues instead of failing -------------------------------
            //
            // The same 10-at-once burst that got 5 requests REJECTED in the "Without" project.
            // The pool caps itself at 3, so the database's limit of 5 is never even approached —
            // and the requests that can't get a connection immediately just WAIT.
            System.out.println();
            System.out.println("=== the same traffic spike: 10 concurrent requests, pool max 3 ===");

            AtomicInteger failures = new AtomicInteger();
            AtomicInteger completed = new AtomicInteger();
            CountDownLatch done = new CountDownLatch(10);

            ExecutorService executor = Executors.newFixedThreadPool(10);
            for (int i = 1; i <= 10; i++) {
                final int request = i;
                executor.submit(() -> {
                    DatabaseConnection connection = null;
                    try {
                        connection = pool.acquire();          // blocks if all 3 are lent out
                        connection.query("SELECT * FROM users WHERE id = " + request);
                        completed.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (IllegalStateException e) {
                        failures.incrementAndGet();
                        System.out.println("    request-" + request + " ✗ " + e.getMessage());
                    } finally {
                        pool.release(connection);
                        done.countDown();
                    }
                });
            }
            done.await();
            executor.shutdown();

            System.out.println("  completed : " + completed.get() + " of 10   ✅ (5 were REJECTED without the pool)");
            System.out.println("  rejected  : " + failures.get());
            System.out.println("  live connections to the database: " + DatabaseConnection.liveConnections()
                    + "   ✅ never above the pool's cap of 3, let alone the server's 5");
            System.out.println("  ✅ Under load this QUEUES. The 'Without' version FAILED.");
            System.out.println("     That backpressure is the win people forget the pool is giving them.");

            // --- The hazard the pool has to defend against ---------------------------------
            //
            // A pooled object is handed to a STRANGER next. Whatever the last borrower left on it
            // goes with it — unless the pool wipes it. This is the nastiest bug class in pooling,
            // because it only appears under load and looks like data corruption.
            System.out.println();
            System.out.println("=== why release() must reset ===");
            DatabaseConnection mine = pool.acquire();
            mine.setSessionUser("alice");
            System.out.println("    borrower A sets sessionUser = " + mine.getSessionUser());
            pool.release(mine);

            DatabaseConnection yours = pool.acquire();
            System.out.println("    borrower B gets conn-" + yours.getId()
                    + ", sessionUser = " + yours.getSessionUser() + "   ✅ wiped by release()");
            System.out.println("    ⚠ Without that reset(), borrower B would be running as alice.");
            pool.release(yours);

            System.out.println();
            System.out.println("=== shutting the pool down ===");
        }

        System.out.println();
        System.out.println("✅ THE ONE IDEA: the pool owns the lifecycle; the client only borrows.");
        System.out.println("   Callers never write `new DatabaseConnection()` and never call close().");
        System.out.println("   That is what makes reuse, the cap, and the backpressure possible at all.");
        System.out.println();
        System.out.println("⚠ THE PRICE: a borrowed object MUST come back. Every acquire() needs a");
        System.out.println("  release() in a finally block — a forgotten one is a leak, and enough of");
        System.out.println("  them starve the pool until every request hangs. Manual lifecycle");
        System.out.println("  management is exactly the thing garbage collection saved you from,");
        System.out.println("  and pooling hands it straight back.");
    }

}
