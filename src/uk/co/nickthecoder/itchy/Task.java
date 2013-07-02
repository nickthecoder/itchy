package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.util.SkipMessage;

public abstract class Task
{
    private boolean skip = false;

    public void sleep( double seconds )
    {
        this.setSkip(true);
        delay(seconds);
    }

    public void delay( double seconds )
    {
        Itchy.singleton.gameLoopJob.sleep((long) (1000 * seconds));
        if (getAbort()) {
            throw new SkipMessage();
        }
    }

    /**
     * Should the task be aborted when it wakes up? For Actor's Behaviours, it will abort if the
     * actor has died while it slept.
     */
    public abstract boolean getAbort();

    public abstract void run();

    public void setSkip( boolean value )
    {
        this.skip = value;
    }

    public boolean getSkip()
    {
        return this.skip;
    }

}
