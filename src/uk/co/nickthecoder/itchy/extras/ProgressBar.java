/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class ProgressBar extends AbstractRole
{
    @Property(label = "From")
    public double from = 0;

    @Property(label = "To")
    public double to = 1;

    @Property(label = "Access", hint = "from Game Bean")
    public String access;

    @Property(label = "Update Period")
    public double updatePeriod = 0.0;

    @Property(label="Horizontal")
    public boolean horizontal = true;
    
    private Timer timer;

    private BeanHelper beanHelper;

    @Override
    public void onBirth()
    {
        this.beanHelper = new BeanHelper(Itchy.getGame(), this.access);

        if (this.updatePeriod > 0) {
            this.timer = Timer.createTimerSeconds(this.updatePeriod);
        }
        update();
    }

    @Override
    public void tick()
    {
        if (this.timer != null) {
            if (this.timer.isFinished()) {
                this.update();
            }
        }
    }

    public void update()
    {
        try {
            double value = ((Number) this.beanHelper.get()).doubleValue();
            setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setValue( double value )
    {
        Surface surface = getActor().getAppearance().getPose().getSurface();

        double ratio = (value - this.from) / (this.to - this.from);

        Rect rect;
        if ( horizontal ) {
            rect = new Rect(0,0, (int) (ratio * surface.getWidth()), surface.getHeight());
        } else {
            rect = new Rect(0,0, surface.getWidth(), (int) (ratio * surface.getHeight()));
        }

        getActor().getAppearance().setClip(rect);
    }
}
