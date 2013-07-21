/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Allows a job to be broken down into a set of small tasks, and for each of the task to be run
 * sequentially. However, any one of the tasks can ask to sleep for a while, and another tasks will
 * take up the baton. In essence, Job uses multiple threads to allow for multiple call stacks, but
 * only one of the threads will run at any given time (the one which holds the Job's monitor).
 * 
 * This class is here solely as a base class for ItchJob, to split the complexity into two.
 */
public abstract class Job implements Runnable
{
    private Lock lock = new ReentrantLock();

    private volatile Thread mainWorker;

    private volatile boolean complete;

    private Thread lockingThread;

    public Job()
    {
    }

    /**
     * Do not call this directly, call start to begin the process. You should not override this
     * method either; client code should be restricted to the doTask method. For internal use only,
     * and is only public to uphold the Runnable interface, which is used when creating the new
     * threads.
     */
    @Override
    public void run()
    {
        this.lock();
        try {
            while (true) {
                if (!doTask()) {
                    this.complete = true;
                    return;
                }
                if (!isMainWorker()) {
                    return;
                }
            }
        } finally {
            this.unlock();
        }
    }

    public void lock()
    {
        this.lock.lock();
        if ( (this.lockingThread != null) && (this.lockingThread != Thread.currentThread()) ) {
            throw new RuntimeException( "Obtained a lock when another thread has it.");
        }
        this.lockingThread = Thread.currentThread();
    }

    public void unlock()
    {
        if (hasLock()) {
            this.lockingThread = null;
            this.lock.unlock();
        } else {
            throw new RuntimeException( "Tried to unlock without first having the lock" );
        }
    }

    /**
     * Do a small amount of work, which makes up the larger job.
     * 
     * @return true there was work to be done, false if there was no work to be done.
     */
    public abstract boolean doTask();

    public void start()
    {
        this.complete = false;

        this.lock();
        try {
            this.mainWorker = new Thread(this);
            this.mainWorker.setDaemon(true);
            this.mainWorker.start();
        } finally {
            this.unlock();
        }
        
        while (!this.complete) {
            this.lock();
            this.unlock();
            try {
                this.mainWorker.join(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public void sleep( long millis )
    {
        if (this.lockingThread != Thread.currentThread()) {
            throw new RuntimeException("Attempt to call sleep from outside of a running task");
        }
        // Create a new thread to continue the work.
        this.mainWorker = new Thread(this);
        this.mainWorker.setDaemon(true);
        this.mainWorker.start();

        long wakeTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < wakeTime) {
            try {
                this.unlock();
                Thread.sleep(wakeTime - System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.lock();
            }
        }
    }

    public boolean isMainWorker()
    {
        return Thread.currentThread() == this.mainWorker;
    }

    public boolean isComplete()
    {
        return this.complete;
    }
    public boolean hasLock()
    {
        return this.lockingThread == Thread.currentThread();
    }
}
