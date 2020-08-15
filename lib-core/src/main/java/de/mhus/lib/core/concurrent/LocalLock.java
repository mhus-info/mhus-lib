package de.mhus.lib.core.concurrent;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.logging.ITracer;
import de.mhus.lib.core.service.LockManager;
import io.opentracing.Scope;

public class LocalLock implements Lock {

    protected Thread lock = null;
    protected String name;
    protected long lockTime = 0;
    protected long cnt = 0;
    protected String stacktrace;

    public LocalLock() {}

    public LocalLock(String name) {
        setName(name);
        register();
    }

    protected void register() {
        M.l(LockManager.class).register(this);
    }

    @SuppressWarnings("resource")
    @Override
    public Lock lock() {
        Scope scope = null;
        try {
            synchronized (this) {
                while (isLocked()) {
                    if (scope != null)
                        scope = ITracer.get().enter("waitUntilUnlock", "name", getName());
                    try {
                        wait();
                    } catch (InterruptedException e) {

                    }
                }
                lock = Thread.currentThread();
                stacktrace = MCast.toString("", lock.getStackTrace());
                lockTime = System.currentTimeMillis();
                cnt++;
                lockEvent(true);
            }
        } finally {
            if (scope != null) scope.close();
        }
        return this;
    }

    /**
     * Overwrite this to get the lock events.
     *
     * @param locked
     */
    protected void lockEvent(boolean locked) {}

    @SuppressWarnings("resource")
    @Override
    public boolean lock(long timeout) {
        Scope scope = null;
        try {
            synchronized (this) {
                long start = System.currentTimeMillis();
                while (isLocked()) {
                    if (scope != null)
                        scope = ITracer.get().enter("waitUntilUnlock", "name", getName());
                    try {
                        wait(timeout);
                    } catch (InterruptedException e) {
                    }
                    if (System.currentTimeMillis() - start >= timeout) return false;
                }
                lock = Thread.currentThread();
                stacktrace = MCast.toString("", lock.getStackTrace());
                lockTime = System.currentTimeMillis();
                lockEvent(true);
                return true;
            }
        } finally {
            if (scope != null) scope.close();
        }
    }

    /**
     * Unlock if the current thread is also the owner.
     *
     * @return true if unlock was successful
     */
    @Override
    public boolean unlock() {
        synchronized (this) {
            if (lock != Thread.currentThread()) return false;
            lockEvent(false);
            lock = null;
            stacktrace = null;
            lockTime = 0;
            notify();
            return true;
        }
    }

    /** Unlock in every case !!! This can break a locked area. */
    @Override
    public void unlockHard() {
        synchronized (this) {
            lockEvent(false);
            lock = null;
            stacktrace = null;
            lockTime = 0;
            notify();
        }
    }

    @Override
    public boolean isLocked() {
        return lock != null;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getOwner() {
        return lock == null ? null : lock.getId() + " " + lock.toString();
    }

    @Override
    public String toString() {
        return name + (lock != null ? " " + lock.getName() : "");
    }

    @Override
    public long getLockTime() {
        return lockTime;
    }

    @Override
    public boolean refresh() {
        return isLocked();
    }

    @Override
    public long getCnt() {
        return cnt;
    }

    @Override
    public String getStartStackTrace() {
        return stacktrace;
    }
}
