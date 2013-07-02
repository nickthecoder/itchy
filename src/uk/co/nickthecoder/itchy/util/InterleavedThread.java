package uk.co.nickthecoder.itchy.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class InterleavedThread extends Thread
{
    // private InterleavedThreads parent;

    public Lock lock;

    boolean pending;

    public InterleavedThread() // InterleavedThreads parent )
    {
        this.lock = new ReentrantLock();
        // this.parent = parent;
    }

    @Override
    public void run()
    {
        this.lock.lock();
        this.work();
        this.pending = false;
    }

    public void next()
    {
        // We have done our morsel of work
        this.pending = false;

        // Allow the controlling thread to take back control
        this.lock.unlock();
        Thread.yield();
        // parent.interrupt();

        // When I'm allowed, take back control
        while (true) {
            if (this.pending) {
                try {
                    this.lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    System.out.println("IT interrupted");
                    System.out.println("Interupted? " + this.isInterrupted());
                }
                System.out.println("Interupted Now? " + this.isInterrupted());
                return;
            } else {
                Thread.yield();
            }
        }
    }

    public abstract void work();
}
