/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

public class CostumeSceneActor extends SceneActor
{
    public Costume costume;

    public CostumeSceneActor( Costume costume )
    {
        this.costume = costume;
    }

    public CostumeSceneActor( Actor actor )
    {
        super(actor);
        this.costume = actor.getCostume();
    }

    @Override
    public Actor createActor( Resources resources, boolean designActor )
    {
        String event = designActor ? "default" : this.startEvent;
        Actor actor = new Actor(this.costume, event);
        if ((this.activationDelay==0) && (!designActor)) {
            actor.event(event);
        }
        
        this.updateActor(actor, resources, designActor);
        
        return actor;
    }

}
