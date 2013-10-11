/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.itchy.util.Property;

/**
 * Displays the value on the screen, which is automatically updated.
 *
 * Uses any JavaBean style properties from your Game object.
 */
public class TextValue extends Behaviour
{
    @Property(label = "Acess")
    /**
     * The java bean of of your Game object. For example, if you have a public attribute called "sceneName",
     * or a public method called "getSceneName", then this would value should be "sceneName"
     * (not getSceneName).
     * 
     * You can traverse from one object to another. For example, Game has a methods called getSceneBehaviour,
     * so if your SceneBehaviour has an attribute called "for" (or a method called "getFoo"), then you can
     * get to it using : <code>"sceneBehaviour.foo"</code>.
     */
    public String access;

    /**
     * What to display if the retrieving the value results in an Exception being thrown.
     * The default is an empty string, which can be confusing if {@link access} is wrong, because you
     * then get nothing displayed! However, in this case, you should see a stack track in System.err,
     * unless you have turned it off using the {@link quiet} attribute.
     */
    @Property(label = "Error Value")
    public String errorValue = "";

    @Property(label = "Null Value")
    /**
     * What to display if the value is null, the default is an empty string.
     */
    public String nullValue = "";

    @Property(label = "Update Period (0 for continuous")
    /**
     * How often to update. The default is zero, which means it is updated every frame. This is fine
     * unless the value take a long time to calculate, in which case, you may want to update it
     * less frequently.
     * A negative value means it will only retrieve the value when the actor is first activated.
     * This is handy for values you know won't need updating. The example game Tetra uses a negative
     * update interval for the High Score.
     */
    public int updateInterval;

    @Property(label = "Quiet")
    /**
     * If false, then a stack trace will written to System.err. This is useful for debugging. 
     */
    public boolean quiet;
    
    private BeanHelper beanHelper;

    private Timer timer;
    
    @Override
    public void onActivate()
    {
        this.beanHelper = new BeanHelper(Itchy.getGame(), this.access);
        if ( updateInterval > 0 ) {
            timer = Timer.createTimerSeconds( this.updateInterval);
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
            if (!quiet) {
                e.printStackTrace();
            }
            pose.setText(this.errorValue);
        }
        if ( updateInterval < 0 ) {
            getActor().deactivate();
        }
    }

    protected String formatValue( Object object )
    {
        return object.toString();
    }
    
}
