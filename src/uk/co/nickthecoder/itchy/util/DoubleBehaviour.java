/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.text.NumberFormat;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.TextPose;

public abstract class DoubleBehaviour extends Behaviour
{
    public NumberFormat format;

    public DoubleBehaviour()
    {
        this(0);
    }

    public DoubleBehaviour( int decimals )
    {
        this.format = NumberFormat.getInstance();
        this.format.setMaximumFractionDigits(decimals);
        this.format.setMinimumFractionDigits(decimals);
    }

    public Actor createActor( Font font, int size )
    {
        TextPose tp = new TextPose("0.0", font, size);
        Actor actor = new Actor(tp);
        actor.setBehaviour(this);
        return actor;
    }

    @Override
    public void tick()
    {
        double value = getValue();
        String str = this.format.format(value);
        TextPose textPose = (TextPose) (getActor().getAppearance().getPose());
        textPose.setText(str);
    }

    public abstract double getValue();

}
