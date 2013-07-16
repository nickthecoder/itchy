/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import uk.co.nickthecoder.itchy.AbstractTextPose;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;

public class TextBehaviour extends Behaviour
{
    public AbstractTextPose textPose;

    public TextBehaviour( AbstractTextPose pose )
    {
        this.textPose = pose;
    }

    public void setText( String text )
    {
        this.textPose.setText(text);
    }

    @Override
    public void tick()
    {
    }

    public Actor createActor()
    {
        Actor actor = new Actor(this.textPose);
        actor.setBehaviour(this);
        
        return actor;
    }

}
