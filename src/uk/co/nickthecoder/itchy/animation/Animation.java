package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public interface Animation extends Cloneable
{
    public String getName();

    public void start( Actor actor );

    public void tick( Actor actor );

    public boolean isFinished();

    public Object clone() throws CloneNotSupportedException;

    public Animation copy();
}
