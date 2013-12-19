/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Actor;

public final class UndoMoveActor implements Undo
{
    private Actor actor;

    private double dx;

    private double dy;

    public UndoMoveActor( Actor actor, double dx, double dy )
    {
        this.actor = actor;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void undo()
    {
        this.actor.moveBy(-this.dx, -this.dy);
    }

    @Override
    public void redo()
    {
        this.actor.moveBy(this.dx, this.dy);
    }

    @Override
    public boolean merge( Undo o )
    {
        if (o instanceof UndoMoveActor) {
            UndoMoveActor other = (UndoMoveActor) o;
            if (this.actor == other.actor) {
                this.dx += other.dx;
                this.dy += other.dy;
                return true;
            }
        }
        return false;
    }

}
