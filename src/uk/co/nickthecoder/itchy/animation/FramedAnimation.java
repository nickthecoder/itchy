/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;

public class FramedAnimation extends AbstractAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(FramedAnimation.class);

    @Property(label="Ping Pong")
    public boolean pingPong;

    List<Frame> frames;

    private List<Frame> readOnlyFrames;

    protected int frameIndex;

    protected int delay;

    protected int direction = 1;

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }
    
    public void addFrame( Frame frame )
    {
        this.frames.add(frame);
        if (this.frames.size() == 1) {
            this.delay = frame.getDelay();
        }
    }

    public FramedAnimation()
    {
        this.frames = new ArrayList<Frame>();
        this.readOnlyFrames = Collections.unmodifiableList(this.frames);
        this.frameIndex = 0;
        this.delay = 0;
    }

    @Override
    public String getName()
    {
        return "Frames";
    }

    public List<Frame> getFrames()
    {
        return this.readOnlyFrames;
    }

    public void replaceFrames( List<Frame> newFrames )
    {
        this.frames.clear();
        this.frames.addAll(newFrames);
        this.readOnlyFrames = Collections.unmodifiableList(this.frames);
    }

    @Override
    public void start( Actor actor )
    {
        this.frameIndex = 0;
        this.direction = 1;
        actor.getAppearance().setPose(this.frames.get(this.frameIndex).getPose());
    }

    @Override
    public void tick( Actor actor )
    {
        if (this.delay > 0) {
            this.delay--;
            return;
        }

        this.nextFrame();
        if (!this.isFinished()) {
            actor.getAppearance().setPose(this.frames.get(this.frameIndex).getPose());
            this.delay = this.frames.get(this.frameIndex).getDelay();
        }

        super.tick(actor);
    }

    private void nextFrame()
    {
        this.frameIndex += this.direction;
        if (this.pingPong && (this.frameIndex >= this.frames.size())) {
            this.frameIndex = this.frames.size() - 2;
            this.direction = -1;
        }
    }

    @Override
    public Animation clone()
    {
        try {
            FramedAnimation result = (FramedAnimation) super.clone();

            result.frames = new ArrayList<Frame>();
            for (Frame frame : this.frames) {
                result.frames.add(frame.copy());
            }

            return result;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isFinished()
    {
        return (this.frameIndex < 0) || (this.frameIndex >= this.frames.size());
    }

}
