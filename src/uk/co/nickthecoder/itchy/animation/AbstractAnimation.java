package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;

public abstract class AbstractAnimation implements Animation, Cloneable
{

    private List<AnimationListener> listeners = new ArrayList<AnimationListener>();

    @Override
    public abstract String getName();

    @Override
    public abstract void start( Actor actor );


    @Override
    public abstract boolean isFinished();

    @Override
    public void tick( Actor actor )
    {
        if ( this.isFinished() ) {
            for ( AnimationListener listener : this.listeners ) {
                listener.finished();
            }
        }
    }

    public void addAnimationListener( AnimationListener listener )
    {
        this.listeners.add( listener );
    }
    public void removeAnimationListener( AnimationListener listener )
    {
        this.listeners.remove( listener );
    }

    @Override
    public Object clone()
        throws CloneNotSupportedException
    {
        AbstractAnimation result = (AbstractAnimation) super.clone();
        result.listeners = new ArrayList<AnimationListener>();
        for ( AnimationListener listener : this.listeners ) {
            result.listeners.add( listener );
        }
        return result;
    }

    @Override
    public Animation copy()
    {
        try {
            return (Animation) this.clone();
        } catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            return null;
        }

    }
}
