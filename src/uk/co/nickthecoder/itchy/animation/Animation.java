package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.MessageListener;

public interface Animation extends Cloneable
{
    public String getName();

    public void start( Actor actor );

    public void tick( Actor actor );

    public boolean isFinished();

    public Object clone() throws CloneNotSupportedException;

    public Animation copy();

    public void addAnimationListener( AnimationListener listener );

    public void removeAnimationListener( AnimationListener listener );
    
    public void addMessageListener( MessageListener listener );

    public void removeMessageListener( MessageListener listener );

    public String getFinishedMessage();
    
    public void setFinishedMessage( String message );
    
}
