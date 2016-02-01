/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.extras.Timer;

public class DelayedActivation extends AbstractRole
{
    private Timer delay;

    public Role actualRole;

    private double alpha;

    public DelayedActivation(double seconds, Role role)
    {
        this.delay = Timer.createTimerSeconds(seconds);
        this.actualRole = role;
    }

    @Override
    public void onBirth()
    {
        this.alpha = getActor().getAppearance().getAlpha();
        if (this.delay.period != 0) {
            getActor().getAppearance().setAlpha(0);
        }
    }

    public String getId()
    {
        return this.actualRole.getId();
    }
    
    public void setId( String id )
    {
        this.actualRole.setId(id);
    }

    @Override
    public void tick()
    {
        if (this.delay.isFinished()) {
            getActor().getAppearance().setAlpha(this.alpha);

            getActor().event(getActor().getStartEvent());
            getActor().setRole(this.actualRole);

            // The normal way that role.birth is called happened to this DelayedActivation,
            // but we want it to be called for the actual role as well...
            this.actualRole.born();

        }
    }
}
