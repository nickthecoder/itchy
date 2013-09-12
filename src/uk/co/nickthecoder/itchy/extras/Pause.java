/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class Pause
{
    private boolean paused;

    public boolean isPaused()
    {
        return this.paused;
    }

    public void togglePause()
    {
        if (this.paused) {
            unpause();
        } else {
            pause();
        }
    }

    public void pause()
    {
        if (this.paused) {
            return;
        }

        this.paused = true;

        for (Actor actor : Actor.allByTag("active")) {
            if (pauseActor(actor)) {
                actor.setBehaviour(new PausedBehaviour(actor.getBehaviour()));
            }
        }
        onPause();
    }

    public void unpause()
    {
        if (this.paused == false) {
            return;
        }

        this.paused = false;

        for (Actor actor : Actor.allByTag("active")) {
            if (actor.getBehaviour() instanceof PausedBehaviour) {
                ((PausedBehaviour) actor.getBehaviour()).unpause();
            }
        }
        onUnpaused();
    }

    public boolean pauseActor( Actor actor )
    {
        return true;
    }

    public void onPause()
    {
    }

    public void onUnpaused()
    {
    }

    private class PausedBehaviour extends Behaviour
    {
        private Behaviour oldBehaviour;

        public PausedBehaviour( Behaviour oldBehaviour )
        {
            this.oldBehaviour = oldBehaviour;
        }

        public void unpause()
        {
            getActor().setBehaviour(this.oldBehaviour);
        }

        @Override
        protected void tickHandler()
        {
            // Do nothing
        }

        @Override
        public void tick()
        {
            // Do Nothing
        }
    }
}
