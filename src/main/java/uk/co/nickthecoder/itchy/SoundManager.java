/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;



/**
 * Manages the sounds that Actors make via the Actor.event(String eventName) method. A costume can specify a sound effect for a given event
 * name, and this sound will play when that event is fired (using actor.event( eventName )). If there aren't enough resources to play the
 * sound, then lower priority sounds will be stopped, i.e. high priority sounds get preferential treatment.
 */
public interface SoundManager
{
    public void tick();

    public void play( Actor actor, String eventName, ManagedSound ms );
    
    public void end( Actor actor, String eventName );

    public void stopAll();

}
