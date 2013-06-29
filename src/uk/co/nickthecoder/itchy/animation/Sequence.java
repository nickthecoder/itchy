package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Actor;

public class Sequence implements SequenceOrParallel
{
    private final CompoundAnimation compoundAnimation;

    private Animation currentAnimation;

    private int index;

    public Sequence( CompoundAnimation ca, Actor actor )
    {
        this.compoundAnimation = ca;
    }

    @Override
    public void start( Actor actor )
    {
        this.index = 0;
        this.currentAnimation = this.compoundAnimation.children.get( this.index );
        this.currentAnimation.start( actor );
    }

    @Override
    public void tick( Actor actor )
    {
        this.currentAnimation.tick( actor );
        if ( this.currentAnimation.isFinished() ) {
            this.index++;
            if ( this.index < this.compoundAnimation.children.size() ) {
                this.currentAnimation = this.compoundAnimation.children.get( this.index );
                this.currentAnimation.start( actor );
            } else {
                this.currentAnimation = null;
            }
        }
    }

    @Override
    public boolean isFinished()
    {
        return this.currentAnimation == null;
    }

    @Override
    public SequenceOrParallel copy()
    {
        SequenceOrParallel result;
        try {
            result = (SequenceOrParallel) super.clone();

        } catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

}
