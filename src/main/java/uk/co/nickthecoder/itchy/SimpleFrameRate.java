/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

/**
 * Keeps the frame rate at a constant value, so that each Actor's tick is for a known, and consistent time interval. The default frame rate
 * is 60fps, but this can be changed by the game designer by :
 * 
 * <pre>
 * <code>
 * Itchy.frameRate.setRequiredRate( 60 )
 * </code>
 * </pre>
 */
public class SimpleFrameRate implements FrameRate
{
    private static final int NANOS_TO_MILLIS = 1000000;

    private static final int NANOS_TO_SECONDS = 1000000000;

    private int requiredRate;

    private int requiredPeriodNanos;

    public boolean isRunning;
    
    /**
     * The maximum number of consecutive frames to skip redrawing when the frame rate is too low.
     */
    public int droppedFramesLimit = 5;

    /**
     * The total number of dropped frames.
     */
    private int droppedFrames = 0;

    private int sequentialDroppedFrames = 0;

    /**
     * The amount of time away from the perfect frame rate. A negative number means we are ahead of schedule. A positive number means we are
     * lagging, the time taken was more than the required period.
     */
    private int errorNanos;

    /**
     * The time at the end of the previous frame
     */
    private long previousNanoTime;

    public SimpleFrameRate()
    {
        this(60);
    }

    public SimpleFrameRate( int framesPerSecond )
    {
        this.requiredRate = framesPerSecond;
        this.requiredPeriodNanos = NANOS_TO_SECONDS / this.requiredRate;
        this.errorNanos = 0;
    }

    @Override
	public void reset()
    {
        this.errorNanos = 0;
        this.droppedFrames = 0;
        this.sequentialDroppedFrames = 0;
    }

	@Override
	public double getFrameRate()
	{
        return this.requiredRate;
    }

    /**
     * @return The total number of dropped frames.
     */
    @Override
	public int getDroppedFrames()
    {
        return this.droppedFrames;
    }

    @Override
	public void loop()
    {
    	isRunning = true;
        this.previousNanoTime = System.nanoTime();

        while (isRunning) {

            doGameLogic();

            // Should we redraw the screen?
            if (this.errorNanos > this.requiredPeriodNanos) {
                this.sequentialDroppedFrames++;
                // Hard dropping a frame. Is this good?
                this.errorNanos = 0;

                if (this.sequentialDroppedFrames >= this.droppedFramesLimit) {
                    System.err.println("Maximum frame skipping, cannot maintain frame rate.");
                    doRedraw();
                    this.sequentialDroppedFrames = 0;
                } else {
                    // System.err.println( "Dropped a frame" );
                    this.droppedFrames++;
                }
            } else {
                doRedraw();
                this.sequentialDroppedFrames = 0;
            }

            completeFrame();
        }
    }

    @Override
	public void end()
    {
    	isRunning = false;
    }
    
    private void completeFrame()
    {
        // The time the frame actually took, including in the error from previous frame(s).
        long frameTimeNanos = this.errorNanos + System.nanoTime() - this.previousNanoTime;

        if (frameTimeNanos < this.requiredPeriodNanos) {
            // We finished the frame with time to spare

            int sleep = (int) ((this.requiredPeriodNanos - frameTimeNanos) / NANOS_TO_MILLIS);

            // Don't bother sleeping if its very close to zero.
            // Thread.sleep doesn't guarantee it can sleep for short periods, and during my tests,
            // it took at least 4ms when trying to sleep for 1ms.
            while (sleep > 3) {
                try {
                    Thread.sleep(sleep);
                    break;
                } catch (InterruptedException e) {
                    // System.err.println( e );
                } finally {
                    // Recalculate the sleep again
                    frameTimeNanos = this.errorNanos + System.nanoTime() - this.previousNanoTime;
                    sleep = (int) ((this.requiredPeriodNanos - frameTimeNanos) / NANOS_TO_MILLIS);
                }
            }
        }

        this.errorNanos = (int) (frameTimeNanos - this.requiredPeriodNanos);

        this.previousNanoTime = System.nanoTime();
    }


    public void doGameLogic()
    {
    	Itchy.processEvents();
        Itchy.tick();
    }

    public void doRedraw()
    {
        Itchy.render();
    }

}
