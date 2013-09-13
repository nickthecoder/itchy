/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

/*
 * Thanks to Colin Fahey, for an excellent guide to all things tetrisy :
 * http://www.colinfahey.com/tetris/
 */

package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.itchy.util.Property;

public class TextValue extends Behaviour
{
    @Property(label = "Acess")
    public String access;

    @Property(label = "Error Value")
    public String errorValue = "";

    @Property(label = "Null Value")
    public String nullValue = "";

    @Property(label = "Update Period (0 for continuous")
    public int updateInterval;

    private BeanHelper beanHelper;

    private Timer timer;
    
    @Override
    public void onActivate()
    {
        this.beanHelper = new BeanHelper(Itchy.singleton.getGame(), this.access);
        if ( updateInterval > 0 ) {
            timer = new Timer( this.updateInterval);
        }
        tick();
        if ( updateInterval < 0 ) {
            getActor().deactivate();
        }
    }

    @Override
    public void tick()
    {
        if ( timer!= null) {
            if (timer.isFinished()) {
                timer.reset();
            } else {
                return;
            }
        }
        
        TextPose pose = (TextPose) getActor().getAppearance().getPose();

        try {
            Object value = this.beanHelper.get();
            if (value == null) {
                pose.setText(this.nullValue);
            } else {
                pose.setText(formatValue(value));
            }
        } catch (Exception e) {
            pose.setText(this.errorValue);
        }
    }

    protected String formatValue( Object object )
    {
        return object.toString();
    }
    
}
