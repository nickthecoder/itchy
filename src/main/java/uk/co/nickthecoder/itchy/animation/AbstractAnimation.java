/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.StringUtils;

public abstract class AbstractAnimation implements Animation, Cloneable
{
    protected static final List<Property<Animation, ?>> properties = new ArrayList<Property<Animation, ?>>();
    
    static {
        properties.add( new StringProperty<Animation>( "finishedMessage" ));
    }
    
    private List<AnimationListener> listeners = new ArrayList<AnimationListener>();

    private String finishedMessage = null;
    
    @Override
    public List<Property<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public abstract String getName();


    @Override
    public String getTagName()
    {
        return getName().toLowerCase().replaceAll(" ", "");
    }

    @Override
    public abstract void start( Actor actor );

    @Override
    public abstract boolean isFinished();

    @Override
    public void tick( Actor actor )
    {
        if (this.isFinished()) {
            fireFinished(actor);
        }
    }

    protected void fireFinished( Actor actor )
    {

        for (AnimationListener listener : this.listeners) {
            listener.finished();
        }
        if (!StringUtils.isBlank(this.finishedMessage)) {
            actor.getRole().onMessage(getFinishedMessage());
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
    public Object clone() throws CloneNotSupportedException
    {
        AbstractAnimation result = (AbstractAnimation) super.clone();

        result.listeners = new ArrayList<AnimationListener>();
        for (AnimationListener listener : this.listeners) {
            result.listeners.add(listener);
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
