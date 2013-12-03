/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Actor;

public class UndoRotateActor implements Undo
{

    private Actor actor;

    private double startHeading;

    private double startDirection;

    private double endHeading;

    private double endDirection;

    public UndoRotateActor( Actor actor )
    {
        this.actor = actor;
        this.startHeading = actor.getHeading();
        this.startDirection = actor.getAppearance().getDirection();
    }

    public void end( UndoList list )
    {
        this.endHeading = this.actor.getHeading();
        this.endDirection = this.actor.getAppearance().getDirection();

        if ((this.endHeading != this.startHeading) || (this.endDirection != this.startDirection)) {
            list.add(this);
        }
    }

    @Override
    public void undo()
    {
        this.actor.setHeading(this.startHeading);
        this.actor.getAppearance().setDirection(this.startDirection);
    }

    @Override
    public void redo()
    {
        this.actor.setHeading(this.endHeading);
        this.actor.getAppearance().setDirection(this.endDirection);
    }

    @Override
    public boolean merge( Undo o )
    {
        if (o instanceof UndoRotateActor) {
            UndoRotateActor other = (UndoRotateActor) o;
            if (this.actor == other.actor) {
                this.endHeading = other.endHeading;
                this.endDirection = other.endDirection;
                return true;
            }
        }
        return false;
    }

}
