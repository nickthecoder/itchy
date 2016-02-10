/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.extras.Timer;

public class DelayedActivation extends AbstractRole
{
    private Timer delay;

    private double seconds;

    public Role actualRole;

    private double alpha;

    public DelayedActivation(double seconds, Role role)
    {
        this.seconds = seconds;
        this.actualRole = role;
    }

    @Override
    public void born()
    {
        // Do not call super, because we aren't really being born, and don't want to be given a collision strategy
        this.delay = Timer.createTimerSeconds(this.seconds);
        this.alpha = getActor().getAppearance().getAlpha();
        if (this.delay.period != 0) {
            getActor().getAppearance().setAlpha(0);
        }
    }

    @Override
    public String getId()
    {
        return this.actualRole.getId();
    }

    @Override
    public void setId(String id)
    {
        this.actualRole.setId(id);
    }

    @Override
    public void tick()
    {
        if (this.delay.isFinished()) {
            getActor().getAppearance().setAlpha(this.alpha);

            getActor().activate( this.actualRole );

        }
    }
}
