package com.thecodeexperience.WithObjectPoolDesignPattern;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * THE REUSABLE OBJECT — the same expensive connection as the "Without" project, with two things
 * added that a pooled object always needs:
 *
 *   reset()     — because a pooled object is HANDED TO A STRANGER next. Anything the last
 *                 borrower left on it (a session variable, an open transaction, a dirty flag) is
 *                 now a bug waiting to happen. See the note on reset() below — this is the single
 *                 most dangerous part of pooling.
 *
 *   isValid()   — because a pooled object can go STALE while it sits idle. The database will
 *                 happily drop a connection that has been quiet for 10 minutes, and the pool will
 *                 happily hand you the corpse. A pool that never validates is a pool that returns
 *                 dead objects.
 *
 * Notice what did NOT change: the 120ms construction cost is still here, in full. **The pool does
 * not make creation cheaper. It makes it RARER.**
 */
public class DatabaseConnection {

    private static final int SERVER_MAX_CONNECTIONS = 5;

    private static final AtomicInteger liveConnections = new AtomicInteger();
    private static final AtomicInteger totalEverCreated = new AtomicInteger();

    private final int id;
    private boolean open;
    private int queriesServed;

    /** Left-over state from the last borrower. The reason reset() exists. */
    private String sessionUser;

    DatabaseConnection() {
        int live = liveConnections.incrementAndGet();
        if (live > SERVER_MAX_CONNECTIONS) {
            liveConnections.decrementAndGet();
            throw new IllegalStateException(
                    "FATAL: too many connections (server max is " + SERVER_MAX_CONNECTIONS + ")");
        }

        this.id = totalEverCreated.incrementAndGet();
        sleep(120);                     // the cost is unchanged — we just pay it far less often
        this.open = true;
        System.out.println("    [conn-" + id + "] OPENED   (handshake + TLS + auth: 120ms)");
    }

    public String query(String sql) {
        if (!open) {
            throw new IllegalStateException("connection is closed");
        }
        sleep(10);
        queriesServed++;
        return "[conn-" + id + "] " + sql;
    }

    public void setSessionUser(String user) {
        this.sessionUser = user;
    }

    public String getSessionUser() {
        return sessionUser;
    }

    /**
     * ⚠ THE MOST IMPORTANT METHOD IN A POOLED CLASS.
     *
     * When an object is returned to the pool it does not die — it goes back on the shelf and is
     * handed to somebody else. If it still carries the last borrower's state, that state has just
     * leaked ACROSS REQUESTS. In a connection pool that means one user's transaction, or one
     * user's session identity, silently becoming another user's.
     *
     * "The pooled object was not reset" is one of the nastiest bug classes there is, because it
     * only shows up under load, and it looks like data corruption rather than a lifecycle bug.
     */
    void reset() {
        this.sessionUser = null;
    }

    /** ⚠ An idle connection can be killed by the server. Never hand out an object you didn't check. */
    boolean isValid() {
        return open;
    }

    void closeForReal() {
        open = false;
        liveConnections.decrementAndGet();
        System.out.println("    [conn-" + id + "] CLOSED   (served " + queriesServed + " queries in its life)");
    }

    public int getId() {
        return id;
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
