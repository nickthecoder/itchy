/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MousePointer;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class SimpleMousePointer implements MousePointer
{
    private Actor actor;

    public SimpleMousePointer( String costumeName )
    {
        this(costumeName, "default");
    }

    public SimpleMousePointer( String costumeName, String startEvent )
    {
        Game game = Itchy.getGame();
        Costume costume = game.resources.getCostume(costumeName);
        this.actor = new Actor(costume);
        this.actor.setRole(createRole());
        game.getGlassStage().add(this.actor);
        this.actor.event(startEvent);

        game.getGlassStage().add(this.actor);
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        this.actor.moveTo(event.x, Itchy.getGame().getHeight() - event.y);
        return false;
    }

    public Role createRole()
    {
        return new PlainRole();
    }

    @Override
    public Actor getActor()
    {
        return this.actor;
    }

}
