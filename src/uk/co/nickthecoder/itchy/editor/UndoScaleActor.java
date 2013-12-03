/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.TextPose;

/**
 * Allows scaling to be undone. Note that the position and scale are both stored, because scaling is done by dragging a corner, and that can
 * change the position of the actor as well as its scale.
 */
public class UndoScaleActor implements Undo
{
    private Actor actor;

    private double startX;

    private double startY;

    private double endX;

    private double endY;

    private double startScale;

    private double endScale;

    private double startFontSize;

    private double endFontSize;

    public UndoScaleActor( Actor actor )
    {
        this.actor = actor;
        this.startX = actor.getX();
        this.startY = actor.getY();
        this.startScale = this.actor.getAppearance().getScale();
        if (actor.getAppearance().getPose() instanceof TextPose) {
            this.startFontSize = ((TextPose) actor.getAppearance().getPose()).getFontSize();
        }
    }

    public void end( UndoList list )
    {
        this.endX = this.actor.getX();
        this.endY = this.actor.getY();
        this.endScale = this.actor.getAppearance().getScale();
        if (this.actor.getAppearance().getPose() instanceof TextPose) {
            this.endFontSize = ((TextPose) this.actor.getAppearance().getPose()).getFontSize();
        }

        if ((this.endX != this.startX) || (this.endY != this.startY) || (this.endScale != this.startScale)) {
            list.add(this);
        }
    }

    @Override
    public void undo()
    {
        this.actor.moveTo(this.startX, this.startY);
        this.actor.getAppearance().setScale(this.startScale);
        if (this.actor.getAppearance().getPose() instanceof TextPose) {
            ((TextPose) this.actor.getAppearance().getPose()).setFontSize(this.startFontSize);
        }
    }

    @Override
    public void redo()
    {
        this.actor.moveTo(this.endX, this.endY);
        this.actor.getAppearance().setScale(this.endScale);
        if (this.actor.getAppearance().getPose() instanceof TextPose) {
            ((TextPose) this.actor.getAppearance().getPose()).setFontSize(this.endFontSize);
        }
    }

    @Override
    public boolean merge( Undo o )
    {
        if (o instanceof UndoScaleActor) {
            UndoScaleActor other = (UndoScaleActor) o;
            if (this.actor == other.actor) {
                this.endX = other.endX;
                this.endY = other.endY;
                this.endScale = other.endScale;
                return true;
            }
        }
        return false;
    }

}
