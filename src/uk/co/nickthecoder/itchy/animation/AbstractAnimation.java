/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.MessageListener;

public abstract class AbstractAnimation implements Animation, Cloneable
{

    private List<AnimationListener> listeners = new ArrayList<AnimationListener>();

    private List<MessageListener> messageListeners = new ArrayList<MessageListener>();

    private String finishedMessage = null;

    @Override
    public abstract String getName();

    @Override
    public abstract void start( Actor actor );

    @Override
    public abstract boolean isFinished();

    @Override
    public void tick( Actor actor )
    {
        if (this.isFinished()) {
            for (AnimationListener listener : this.listeners) {
                listener.finished();
            }
            if (this.finishedMessage != null) {
                for (MessageListener listener : this.messageListeners) {
                    listener.onMessage(this.finishedMessage);
                }
            }
        }
    }

    @Override
    public String getFinishedMessage()
    {
        return this.finishedMessage;
    }

    @Override
    public void setFinishedMessage( String message )
    {
        this.finishedMessage = message;
    }

    @Override
    public void addAnimationListener( AnimationListener listener )
    {
        this.listeners.add(listener);
    }

    @Override
    public void removeAnimationListener( AnimationListener listener )
    {
        this.listeners.remove(listener);
    }

    @Override
    public void addMessageListener( MessageListener listener )
    {
        this.messageListeners.add(listener);
    }

    @Override
    public void removeMessageListener( MessageListener listener )
    {
        this.messageListeners.remove(listener);
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        AbstractAnimation result = (AbstractAnimation) super.clone();
        result.listeners = new ArrayList<AnimationListener>();
        for (AnimationListener listener : this.listeners) {
            result.listeners.add(listener);
        }
        result.messageListeners = new ArrayList<MessageListener>();
        for (MessageListener listener : this.messageListeners) {
            result.messageListeners.add(listener);
        }
        return result;
    }

    @Override
    public Animation copy()
    {
        try {
            return (Animation) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }

    }
}
