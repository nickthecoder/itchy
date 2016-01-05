/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.extras.Timer;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.BooleanProperty;
import uk.co.nickthecoder.itchy.property.DoubleProperty;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.BeanHelper;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class ProgressBar extends AbstractRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new DoubleProperty<Role>("from"));
        properties.add(new DoubleProperty<Role>("to"));
        properties.add(new StringProperty<Role>("access").hint("from Game object"));
        properties.add(new DoubleProperty<Role>("updatePeriod"));
        properties.add(new BooleanProperty<Role>("horizontal"));
    }

    public double from = 0;

    public double to = 1;

    public String access;

    public double updatePeriod = 0.0;

    public boolean horizontal = true;

    private Timer timer;

    private BeanHelper beanHelper;

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

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

    public void setValue(double value)
    {
        Surface surface = getActor().getAppearance().getPose().getSurface();

        double ratio = (value - this.from) / (this.to - this.from);

        Rect rect;
        if (this.horizontal) {
            rect = new Rect(0, 0, (int) (ratio * surface.getWidth()), surface.getHeight());
        } else {
            int amount = (int) (ratio * surface.getHeight());
            rect = new Rect(0, surface.getHeight() - amount, surface.getWidth(), amount);
        }

        getActor().getAppearance().setClip(rect);
    }
}
