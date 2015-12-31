package uk.co.nickthecoder.itchy;

/**
 * FrameRate is in charge of timing each frame, the default implementation {@link SimpleFrameRate} tries to keep a constant
 * frame rate of
 * @author nick
 *
 */
public interface FrameRate
{

	/**
	 * @return The total number of dropped frames.
	 */
	public int getDroppedFrames();

	/**
	 * Returns the current frame rate. For fixed frame rates, such as {@link SimpleFrameRate}, this returns a fixed value.
	 * Other implementations may return the actual frame rate.
	 * 
	 * @return The current frame rate
	 */
	public double getFrameRate();
	
	/**
	 * Reset the dropped frame count - useful if you want to count dropped frames for a single scene only.
	 */
	public void reset();

	/**
	 * Called by Itchy 
	 */
    public void loop();
    
    /**
     * Called by Itchy when the game loop should exit. This can happen when the application is to terminate, but can also happen
     * when this FrameRate implementation is replaced by another. The current FrameRate's end method is called, and the new one's
     * loop method is called.
     */
    public void end();


}