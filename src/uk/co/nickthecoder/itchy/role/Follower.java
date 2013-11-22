/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;

public class Follower extends Companion<Follower>
{
    private boolean followRotatation = false;;

    public Follower( Role following )
    {
        this(following.getActor());
    }

    public Follower( Actor following )
    {
        super(following);
    }

    /**
     * Ensures that the follower always have the same direction as the object its following? When
     * rotate is not called, the Follower's direction will not be changed.
     */
    public Follower followRotatation()
    {
        this.followRotatation = true;
        return this;
    }

    @Override
    public void tick()
    {
        follow();
    }

    private void follow()
    {
        getActor().moveTo(this.source);
        getActor().moveForwards(this.offsetForwards, this.offsetSidewards);
        getActor().moveBy(this.offsetX, this.offsetY);

        if (this.followRotatation) {
            getActor().getAppearance().setDirection(this.source.getAppearance().getDirection());
        }
    }

    @Override
    public Actor createActor()
    {
        Actor result = super.createActor();
        follow();
        return result;
    }

}
