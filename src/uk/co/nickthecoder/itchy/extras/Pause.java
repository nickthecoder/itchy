/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorsLayer;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Game;

public class Pause
{
    private final Game game;
    
    private boolean paused;

    private String costumeName;

    private Actor pauseActor;

    private long totalTimePausedMillis;
    
    /**
     * Remembers the time that the game was last paused, so that timers which don't count down during
     * pauses can use this time when the game is paused.
     */
    private long pauseTimeMillis;
    
    public Pause( Game game )
    {
        this( game, "paused");
    }

    public Pause( Game game, String costumeName )
    {
        this.game = game;
        this.costumeName = costumeName;
    }

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

    /**
     * Remembers the time that the game was last paused, so that timers which don't count down during
     * pauses can use this time when the game is paused.
     */
    public long pauseTimeMillis()
    {
        return pauseTimeMillis;
    }
    
    /**
     * The total time in milliseconds that the game was in a paused state.
     * @return
     */
    public long totalTimePausedMillis()
    {
        return totalTimePausedMillis;
    }
    
    public void pause()
    {
        pause(true);
    }
    
    public void pause( boolean showMessage )
    {        
        if (this.paused) {
            return;
        }

        this.pauseTimeMillis = game.gameTimeMillis();
        this.paused = true;

        for (Actor actor : Actor.allByTag("active")) {
            if (pauseActor(actor)) {
                actor.setBehaviour(new PausedBehaviour(actor.getBehaviour()));
            }
        }

        if ( showMessage ) {
            this.pauseActor = createActor();
            if (this.pauseActor != null) {
                this.pauseActor.activate();
                this.pauseActor.event("pause");
            }
        }
        
        onPause();
    }

    protected Actor createActor()
    {
        Costume costume = game.resources.getCostume(this.costumeName);
        if (costume == null) {
            return null;
        }

        Actor actor = new Actor(costume);
        ActorsLayer layer = game.getPopupLayer();
        actor.moveTo(layer.getWorldRectangle().width / 2, layer.getWorldRectangle().height / 2);
        layer.add(actor);

        return actor;
    }

    public void unpause()
    {
        if (this.pauseActor != null) {
            this.pauseActor.deathEvent("unpause");
            this.pauseActor = null;
        }

        if (this.paused == false) {
            return;
        }

        this.paused = false;

        for (Actor actor : Actor.allByTag("active")) {
            if (actor.getBehaviour() instanceof PausedBehaviour) {
                ((PausedBehaviour) actor.getBehaviour()).unpause();
            }
        }
        
        this.totalTimePausedMillis += game.gameTimeMillis() - this.pauseTimeMillis;
        
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
