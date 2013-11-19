/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.itchy.extras.Timer;

public class DelayedActivation extends AbstractRole
{
    private Timer delay;

    private Role role;

    private double alpha;
    
    public DelayedActivation( double seconds, Role role )
    {
        this.delay = Timer.createTimerSeconds(seconds);
        this.role = role;
    }

    @Override
    public void onAttach()
    {
        this.alpha = getActor().getAppearance().getAlpha();
        getActor().getAppearance().setAlpha(0);
    }

    @Override
    public void tick()
    {
        if (this.delay.isFinished()) {
            getActor().setRole(this.role);
            // Was deactivated and now activated so that the role's onActivate method is called.
            getActor().getAppearance().setAlpha(this.alpha);
            getActor().event(getActor().getStartEvent());
        }
    }
}
