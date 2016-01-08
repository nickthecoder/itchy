/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;

public class CompoundAnimation extends AbstractAnimation
{
    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();

    static {
        properties.add( new BooleanProperty<Animation>( "sequence" ) );
        properties.add( new IntegerProperty<Animation>( "loops" ) );
        properties.addAll( AbstractAnimation.properties );
    }
    
    public List<Animation> children = new ArrayList<Animation>();

    public boolean sequence;

    public int loops;

    private SequenceOrParallel sop;

    private int loopsRemaining;

    public CompoundAnimation( boolean sequence )
    {
        this.sequence = sequence;
        this.loops = 1;
    }

    @Override
    public List<Property<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return this.sequence ? "Sequence" : "Parallel";
    }

    public CompoundAnimation add( Animation child )
    {
        this.addAnimation(child);
        return this;
    }

    public void addAnimation( Animation child )
    {
        this.children.add(child);
    }

    public void removeAnimation( Animation child )
    {
        this.children.remove(child);
    }

    public void moveAnimationUp( Animation child )
    {
        int index = this.children.indexOf(child);
        assert (index > 0);
        Animation other = this.children.get(index - 1);
        this.children.set(index, other);
        this.children.set(index - 1, child);
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);

        this.loopsRemaining = this.loops > 0 ? this.loops : 1;

        if (this.sequence) {
            this.sop = new Sequence(this, actor);
        } else {
            this.sop = new Parallel(this, actor);
        }
        this.sop.start(actor);

    }

    public void startExceptFirst( Actor actor )
    {
        this.loopsRemaining = this.loops > 0 ? this.loops : 1;

        if (this.sequence) {
            this.sop = new Sequence(this, actor);
        } else {
            this.sop = new Parallel(this, actor);
        }
        this.sop.startExceptFirst(actor);

    }

    @Override
    public void tick( Actor actor )
    {
        this.sop.tick(actor);

        if (this.sop.isFinished()) {
            if (this.loops > 0) {
                this.loopsRemaining--;
            }
            if (this.loopsRemaining > 0) {
                this.sop.start(actor);
            }
        }
        super.tick(actor);
    }

    public void fastForward( Actor actor )
    {
        for ( Animation animation : this.children ) {
            animation.fastForward(actor);
        }
        this.loopsRemaining = 0;
        this.fireFinished(actor);
    }
    
    @Override
    public Animation clone() throws CloneNotSupportedException
    {
        assert (this.sop == null); // Can only clone an animation which hasn't started

        CompoundAnimation result = (CompoundAnimation) super.clone();

        result.children = new ArrayList<Animation>();
        for (Animation child : this.children) {
            result.children.add(child.copy());
        }

        return result;
    }

    @Override
    public boolean isFinished()
    {
        return this.loopsRemaining == 0;
    }

}
