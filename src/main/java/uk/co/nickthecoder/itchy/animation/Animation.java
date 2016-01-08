/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.itchy.util.Named;

public interface Animation extends Cloneable, PropertySubject<Animation>, Named
{
    @Override
    public List<Property<Animation, ?>> getProperties();

    @Override
    public String getName();

    public String getTagName();

    public void start( Actor actor );

    public void tick( Actor actor );

    /**
     * Perform the last part of the animation. If it is a compound, then fast forward each part.
     */
    public void fastForward( Actor actor );
    
    public boolean isFinished();

    public Object clone() throws CloneNotSupportedException;

    public Animation copy();

    public void addAnimationListener( AnimationListener listener );

    public void removeAnimationListener( AnimationListener listener );

    public String getFinishedMessage();

    public void setFinishedMessage( String message );

}
