/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimplePause implements Pause
{
    protected String pauseSceneName;

    protected boolean paused;

    private long totalTimePausedMillis;

    private Scene scene;

    /**
     * Remembers the time that the game was last paused, so that timers which don't count down during pauses can use
     * this time when the game is paused.
     */
    private long pauseTimeMillis;

    public SimplePause()
    {
        this("pause");
    }

    public SimplePause(String sceneName)
    {
        this.pauseSceneName = sceneName;
    }

    @Override
    public boolean isPaused()
    {
        return this.paused;
    }

    @Override
    public void togglePause()
    {
        if (this.paused) {
            unpause();
        } else {
            pause();
        }
    }

    /**
     * Remembers the time that the game was last paused, so that timers which don't count down during pauses can use
     * this time when the game is paused.
     */
    @Override
    public long pauseTimeMillis()
    {
        return this.pauseTimeMillis;
    }

    /**
     * @return The total time in milliseconds that the game was in a paused state.
     */
    @Override
    public long totalTimePausedMillis()
    {
        return this.totalTimePausedMillis;
    }

    @Override
    public void pause()
    {
        if (this.paused) {
            System.err.println("Already paused");
            return;
        }

        Game game = Itchy.getGame();

        this.pauseTimeMillis = game.gameTimeMillis();
        this.paused = true;

        for (Iterator<Actor> i = game.getActors(); i.hasNext();) {
            Actor actor = i.next();

            if (pauseActor(actor)) {
                actor.setRole(new PausedRole(actor.getRole()));
            }
        }

        this.scene = game.mergeScene(this.pauseSceneName);
    }

    /**
     * Subclasses may choose not to pause some actors, so example, special effects may be allowed to continue ticking
     * during a pause.
     * 
     * @param actor
     * @return The default implementation always returns true.
     */
    protected boolean pauseActor(Actor actor)
    {
        return true;
    }

    @Override
    public void unpause()
    {
        if (this.paused == false) {
            return;
        }

        this.paused = false;
        Game game = Itchy.getGame();

        game.unmergeScene(this.scene);

        for (Iterator<Actor> i = game.getActors(); i.hasNext();) {
            Actor actor = i.next();

            if (actor.getRole() instanceof PausedRole) {
                ((PausedRole) actor.getRole()).unpause();
            }
        }

        this.totalTimePausedMillis += game.gameTimeMillis() - this.pauseTimeMillis;
    }

    private class PausedRole extends AbstractRole
    {
        private Role oldRole;

        private Set<String> oldTags;

        public PausedRole(Role oldRole)
        {
            this.oldRole = oldRole;
            this.oldTags = new HashSet<String>(oldRole.getTags());
        }

        public void unpause()
        {
            getActor().setRole(this.oldRole);
            for (String tag : this.oldTags) {
                getActor().getRole().addTag(tag);
            }
        }

        @Override
        public void animate()
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
