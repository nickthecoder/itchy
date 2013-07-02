package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public interface SequenceOrParallel extends Cloneable
{
    public void start( Actor actor );

    public void tick( Actor actor );

    public boolean isFinished();

    public abstract SequenceOrParallel copy();
}
