/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.MessageListener;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Named;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.itchy.util.PropertySubject;

public interface Animation extends Cloneable, PropertySubject<Animation>, Named
{
    public List<AbstractProperty<Animation,?>> getProperties();
    
    public String getName();

    public String getTagName();

    public void start( Actor actor );

    public void tick( Actor actor );

    public boolean isFinished();

    public Object clone() throws CloneNotSupportedException;

    public Animation copy();

    public void addAnimationListener( AnimationListener listener );

    public void removeAnimationListener( AnimationListener listener );
    
    public void addMessageListener( MessageListener listener );

    public void removeMessageListener( MessageListener listener );

    @Property(label="Finished Message")
    public String getFinishedMessage();
    
    public void setFinishedMessage( String message );
    
}
