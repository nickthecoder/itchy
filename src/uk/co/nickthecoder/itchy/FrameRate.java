package uk.co.nickthecoder.itchy;

/**
 * Keeps the frame rate at a constant value, so that each Actor's tick is for a known, and
 * consistent time interval. The default frame rate is 50fps, but this can be changed by the game
 * designer by :
 * 
 * <pre>
 * <code>
 * Itchy.singleton.frameRate.setRequiredRate( 60 )
 * </code>
 * </pre>
 */
public abstract class FrameRate
{
    private int requiredRate;

    private double requiredPeriodSeconds;

    private long requiredPeriodNanos;

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
     * The amount of time away from the perfect frame rate. A negative number means we are ahead of
     * schedule. A positive number means we are lagging, the time taken was more than the required
     * period.
     */
    private long errorNanos;

    /**
     * The time at the end of the previous frame
     */
    private long previousNanoTime;

    public FrameRate()
    {
        setRequiredRate(50);
    }

    /**
     * Set the required frame rate. The default is 50, and if you want to change it, do so once in
     * your game's constructor.
     * 
     * @param value
     *        the required frame rate in frames per second.
     */
    public final void setRequiredRate( int value )
    {
        this.requiredRate = value;
        this.requiredPeriodSeconds = 1.0 / this.requiredRate;
        this.requiredPeriodNanos = (long) (1.0E9 / this.requiredRate);
        this.errorNanos = 0;
    }

    public int getRequiredRate()
    {
        return this.requiredRate;
    }

    public double getRequiredPeriodSeconds()
    {
        return this.requiredPeriodSeconds;
    }

    /**
     * @return The number of nanoseconds the last frame was from perfect timing.
     */
    public long getErrorNanos()
    {
        return this.errorNanos;
    }
    /**
     * @return The number of milliseconds the last frame was from perfect timing.
     */
    public int getErrorMillis()
    {
        return (int) (this.errorNanos / 1.0E06);
    }
    
    /**
     * @return The total number of dropped frames.
     */
    public int getDroppedFrames()
    {
        return this.droppedFrames;
    }
    
    void loop()
    {
        this.previousNanoTime = System.nanoTime();

        while (isRunning()) {
            
            doGameLogic();
            
            // Should we redraw the screen?
            if (this.errorNanos > this.requiredPeriodNanos) {
                // We are lagging by over a frame, correct by not doing the redraw.
                this.sequentialDroppedFrames++;
                
                if (this.sequentialDroppedFrames >= this.droppedFramesLimit) {
                    System.err.println("Maximum frame skipping, cannot maintain frame rate.");
                    doRedraw();
                    this.sequentialDroppedFrames = 0;
                } else {
                    System.err.println( "Dropped a frame" );
                    this.droppedFrames++;
                }
            } else {
                doRedraw();
                this.sequentialDroppedFrames = 0;
            }
            
            completeFrame();
        }
    }

    private void completeFrame()
    {
        long frameTimeNanos = this.errorNanos + System.nanoTime() - this.previousNanoTime;

        if (frameTimeNanos < this.requiredPeriodNanos) {
            // We finished the frame with time to spare

            int sleep = (int) ((this.requiredPeriodNanos - frameTimeNanos) / 1.0E6);

            // Don't bother sleeping if its very close to the required rate.
            while (sleep > 1) {
                try {
                    Thread.sleep(sleep);
                    break;
                } catch (InterruptedException e) {
                    // We didn't sleep for the full amount, so lets work out the diff again.
                    System.err.println( e );
                } finally {
                    // Recalculate the sleep again
                    frameTimeNanos = this.errorNanos + System.nanoTime() - this.previousNanoTime;
                    sleep = (int) ((this.requiredPeriodNanos - frameTimeNanos) / 1.0E-6);
                }
            }
        }
        
        this.errorNanos = frameTimeNanos - this.requiredPeriodNanos;

        this.previousNanoTime = System.nanoTime();
    }

    abstract void doGameLogic();

    abstract void doRedraw();

    abstract boolean isRunning();

}
