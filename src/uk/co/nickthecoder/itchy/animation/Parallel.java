/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.Iterator;

import uk.co.nickthecoder.itchy.Actor;

public class Parallel implements SequenceOrParallel
{
    private final CompoundAnimation compoundAnimation;

    public Parallel( CompoundAnimation ca, Actor actor )
    {
        // this.animations = new ArrayList<Animation>();
        this.compoundAnimation = ca;
    }

    @Override
    public void start( Actor actor )
    {
        for (Animation child : this.compoundAnimation.children) {
            child.start(actor);
        }
    }

    @Override
    public void tick( Actor actor )
    {
        for (Iterator<Animation> i = this.compoundAnimation.children.iterator(); i.hasNext();) {
            Animation child = i.next();
            child.tick(actor);
            if (child.isFinished()) {
                i.remove();
            }
        }
    }

    @Override
    public boolean isFinished()
    {
        return this.compoundAnimation.children.size() == 0;
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
