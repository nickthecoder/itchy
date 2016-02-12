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
    public void onAttach()
    {
        this.alpha = getActor().getAppearance().getAlpha();
        if (this.seconds > 0) {
            this.delay = Timer.createTimerSeconds(this.seconds);
            getActor().getAppearance().setAlpha(0);
        }
    }

    @Override
    public void tick()
    {
        if ((this.delay == null) || (this.delay.isFinished())) {
            getActor().getAppearance().setAlpha(this.alpha);

            getActor().setRole( this.actualRole );
        }
    }
}
