/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;


/**
 * Transitions from one scene to another.
 * 
 * It works by taking a snapshot of the current screen, then loading the new {@link uk.co.nickthecoder.itchy.Scene} (which will kill all
 * Actors from the previous scene). It then places the snapshot above the new scene, so the new scene can't be seen. Finally the snapshot is
 * animated, such that it gradually reveals the new scene.
 */
public interface SceneTransition
{
    /**
     * Called before the old scene is cleared, so that it has a chance to take a snapshot of the old scene.
     */
    public void prepare();
    
    /**
     * @return true iff the scene transition has been prepared, but has not yet completed.
     */
    public boolean isActive();
    
    /**
     * Called after the new scene has been loaded, but before the new scene is drawn, and before any tick events
     * happen for the new scene.
     * Typically the new scene will be obscured by a shapshot of the old scene, and the snapshot is gradually
     * removed to reveal the new scene.
     */
    public void begin();
    
    /**
     * If the transition is part way through, then complete the transition immediately, to fully reveal the new scene.
     * This is used if another transition begins before the previous one has ended.
     */
    public void complete();
}
