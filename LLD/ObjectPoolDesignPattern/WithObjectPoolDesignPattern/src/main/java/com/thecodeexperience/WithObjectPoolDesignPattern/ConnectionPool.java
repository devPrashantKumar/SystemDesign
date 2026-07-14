package com.thecodeexperience.WithObjectPoolDesignPattern;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * THE POOL — and the whole idea, in one sentence:
 *
 *     ✅ THE POOL OWNS THE OBJECT'S LIFECYCLE. THE CLIENT ONLY BORROWS IT.
 *
 * In the "Without" project every caller created and destroyed its own connection, which meant
 * (a) the 120ms setup was repaid on every request and (b) there was nowhere that could ever say
 * "that's enough — wait your turn". Both failures had the same root: nobody owned the lifecycle.
 *
 * Here, exactly one object does. Callers never see `new DatabaseConnection()` and never call
 * close(); they acquire() and release(). That single change buys three things at once:
 *
 *     1. REUSE   — a released connection goes back on the shelf, still open. The next borrower
 *                  pays 0ms instead of 120ms.
 *     2. A CAP   — the pool creates at most maxSize objects, ever. It is now structurally
 *                  impossible to exceed the database's limit.
 *     3. BACKPRESSURE — when everything is lent out, the 4th caller WAITS instead of failing.
 *                  Under load this degrades gracefully rather than falling over. That's arguably
 *                  the biggest win, and the one people forget the pool is even doing.
 *
 * Note the pool grows LAZILY: it doesn't create maxSize connections up front, only as demand
 * needs them, up to the cap. (Real pools like HikariCP also keep a minimum idle count warm, and
 * evict connections that have been idle too long.)
 */
public class ConnectionPool implements AutoCloseable {

    private final BlockingQueue<DatabaseConnection> available = new LinkedBlockingQueue<>();
    private final List<DatabaseConnection> allConnections = new CopyOnWriteArrayList<>();

    private final int maxSize;
    private final long timeoutMillis;

    private final AtomicInteger created = new AtomicInteger();
    private final AtomicInteger reused = new AtomicInteger();

    public ConnectionPool(int maxSize, long timeoutMillis) {
        this.maxSize = maxSize;
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * ✅ ACQUIRE — borrow a connection.
     *
     * Three cases, in order of preference:
     *   1. an idle one is on the shelf   → take it. Cost: 0ms. This is the point of the pattern.
     *   2. the shelf is empty but we're below the cap → create one. Cost: 120ms, paid once.
     *   3. the shelf is empty and we're AT the cap → WAIT for someone to give one back.
     *      This is backpressure. Not an error — a queue.
     */
    public DatabaseConnection acquire() throws InterruptedException {
        DatabaseConnection connection = available.poll();

        if (connection == null && created.get() < maxSize) {
            synchronized (this) {
                if (created.get() < maxSize) {
                    created.incrementAndGet();
                    DatabaseConnection fresh = new DatabaseConnection();
                    allConnections.add(fresh);
                    return fresh;
                }
            }
        }

        if (connection == null) {
            // At capacity. Queue up rather than blow up.
            connection = available.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if (connection == null) {
                throw new IllegalStateException(
                        "timed out after " + timeoutMillis + "ms waiting for a connection");
            }
        }

        // ⚠ Never hand out an object you haven't checked. Idle connections go stale.
        if (!connection.isValid()) {
            System.out.println("    [pool] discarding a dead connection, opening a replacement");
            created.decrementAndGet();
            allConnections.remove(connection);
            return acquire();
        }

        reused.incrementAndGet();
        return connection;
    }

    /**
     * ✅ RELEASE — give it back. NOT close(). The object stays alive and open.
     *
     * ⚠ reset() is the load-bearing line. This object is about to be handed to a stranger, and
     *   anything the last borrower left on it would go with it. See DatabaseConnection.reset().
     */
    public void release(DatabaseConnection connection) {
        if (connection == null) {
            return;
        }
        connection.reset();               // ← state from the last borrower dies HERE
        available.offer(connection);
    }

    /** The pool created these, so the pool is the one that destroys them. */
    @Override
    public void close() {
        for (DatabaseConnection connection : allConnections) {
            connection.closeForReal();
        }
        allConnections.clear();
        available.clear();
    }

    public int created() {
        return created.get();
    }

    /** How many acquire() calls were served off the shelf — i.e. cost 0ms instead of 120ms. */
    public int reused() {
        return reused.get();
    }

    public int idle() {
        return available.size();
    }

}
