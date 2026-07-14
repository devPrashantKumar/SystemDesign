package com.thecodeexperience.WithoutObjectPoolDesignPattern;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The expensive object. Two things about it matter, and they are the two things that make an
 * object worth pooling AT ALL:
 *
 *   1. It is EXPENSIVE TO CREATE. A real connection means a TCP handshake, a TLS negotiation and
 *      an auth round-trip — tens to hundreds of milliseconds. Simulated here as a 120ms sleep.
 *
 *   2. It is a SCARCE, SHARED RESOURCE. The database server has a hard cap on connections
 *      (`max_connections`). Exceed it and it doesn't slow down — it REFUSES you.
 *
 * If an object is cheap to make (a String, a DTO, a Point), it is not a pooling candidate. Java's
 * allocator is very fast and the GC is very good; pooling cheap objects makes your code slower and
 * more complicated. This class is worth pooling because of (1) and (2), and for no other reason.
 */
public class DatabaseConnection {

    /** The database server's hard limit. Cross it and the server rejects the connection. */
    private static final int SERVER_MAX_CONNECTIONS = 5;

    private static final AtomicInteger liveConnections = new AtomicInteger();
    private static final AtomicInteger totalEverCreated = new AtomicInteger();

    private final int id;
    private boolean open;

    public DatabaseConnection() {
        int live = liveConnections.incrementAndGet();
        if (live > SERVER_MAX_CONNECTIONS) {
            liveConnections.decrementAndGet();
            throw new IllegalStateException(
                    "FATAL: too many connections (server max is " + SERVER_MAX_CONNECTIONS + ")");
        }

        this.id = totalEverCreated.incrementAndGet();

        // ⚠ THE COST. Paid in full, every single time, by whoever is unlucky enough to be the
        //    request that needed a connection.
        sleep(120);

        this.open = true;
        System.out.println("    [conn-" + id + "] OPENED   (handshake + TLS + auth: 120ms)");
    }

    public String query(String sql) {
        if (!open) {
            throw new IllegalStateException("connection is closed");
        }
        sleep(10);
        return "[conn-" + id + "] result of: " + sql;
    }

    public void close() {
        open = false;
        liveConnections.decrementAndGet();
        System.out.println("    [conn-" + id + "] CLOSED   ← and the 120ms it cost is gone with it");
    }

    public static int liveConnections() {
        return liveConnections.get();
    }

    public static int totalEverCreated() {
        return totalEverCreated.get();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

}
