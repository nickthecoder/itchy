/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.util.Property;

// TODO Is this used? If so, add it to the extras diagram, otherwise, delete it.
public class Pieces extends Behaviour implements Cloneable
{
    @Property(label="Pieces")
    public int pieces = 10;
    
    @Override
    public void onActivate()
    {
        if ( pieces > 0 ) {
            new Fragment()
                .actor(getActor())
                .pieces(30)
                .createPoses("fragment");
    
            for( PoseResource poseResource : getActor().getCostume().getPoseChoices("fragment")) {
                Pose pose = poseResource.pose;
                
                Actor actor = new Actor(pose);
                actor.setBehaviour(createBehaviour());
                actor.moveTo(getActor());
                getActor().getLayer().addTop(actor);
                actor.activate();
            }
            getActor().kill();
        }
    }

    @Override
    public void tick()
    {

    }
    
    protected Behaviour createBehaviour()
    {
        return this.clone();
    }
    
    public Pieces clone()
    {
        Pieces result = (Pieces) super.clone();
        result.pieces = 0;
        return result;
    }
}
