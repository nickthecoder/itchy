package uk.co.nickthecoder.itchy.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InterleavedThreads extends Thread
{

    private List<InterleavedThread> threads;
    private List<InterleavedThread> newThreads;

    private boolean quitting;

    public InterleavedThreads()
    {
        this.threads = new LinkedList<InterleavedThread>();
        this.newThreads = new LinkedList<InterleavedThread>();
    }

    public void add( InterleavedThread thread )
    {
        this.newThreads.add(thread);
        thread.pending = false;
    }

    public void quit()
    {
        this.quitting = true;
    }

    @Override
    public void run()
    {
        this.quitting = false;

        while (!this.quitting) {

            for (InterleavedThread thread : this.newThreads) {
                this.threads.add(thread);
                thread.start();
                try {
                    thread.lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    System.out.println("New thread interrupted");
                }
            }
            this.newThreads.clear();

            // System.out.println( "Start loop" );
            for (Iterator<InterleavedThread> i = this.threads.iterator(); i.hasNext();) {

                InterleavedThread thread = i.next();

                if (thread.isAlive()) {

                    thread.pending = true;

                    // Allow the thread to do work
                    thread.lock.unlock();

                    while (thread.pending) {
                        Thread.yield();
                    }
                    // thread.interrupt();

                    // Regain the lock when it has finished its morsel of work
                    try {
                        thread.lock.lockInterruptibly();
                    } catch (InterruptedException e) {
                        System.out.println("ITs interrupted");
                    }

                } else {
                    i.remove();
                    thread.lock.unlock();
                }

            }
            // System.out.println( "End loop" );

        }
    }

}
