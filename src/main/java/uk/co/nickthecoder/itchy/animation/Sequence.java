/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
        for (this.index = 0; this.index < this.compoundAnimation.children.size(); this.index++) {

            this.currentAnimation = this.compoundAnimation.children.get(this.index);
            if (this.currentAnimation == null) {
                return;
            }
            this.currentAnimation.start(actor);
            if (!this.currentAnimation.isFinished()) {
                break;
            }
        }
    }

    @Override
    public void startExceptFirst( Actor actor )
    {
        this.index = 0;
        this.currentAnimation = this.compoundAnimation.children.get(this.index);
    }

    @Override
    public boolean tick( Actor actor )
    {   
        boolean result = this.currentAnimation.tick(actor);
        
        if (this.currentAnimation.isFinished()) {
            this.index++;
            if (this.index < this.compoundAnimation.children.size()) {
                this.currentAnimation = this.compoundAnimation.children.get(this.index);
                this.currentAnimation.start(actor);
            } else {
                this.currentAnimation = null;
            }
        }
        
        return result;
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

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

}
