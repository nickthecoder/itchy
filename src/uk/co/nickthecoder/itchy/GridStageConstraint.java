/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.Iterator;

public class GridStageConstraint implements StageConstraint
{
    private int boxWidth;
    private int boxHeight;

    public GridStageConstraint( int boxWidth, int boxHeight )
    {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    @Override
    public double constrainX( double requestedX, double requestedY )
    {
        return (int) Math.floor((requestedX + this.boxWidth / 2) / this.boxWidth) * this.boxWidth;
    }

    @Override
    public double constrainY( double requestedX, double requestedY )
    {
        return (int) Math.floor((requestedY + this.boxHeight / 2) / this.boxHeight) * this.boxHeight;
    }

    @Override
    public void added( Actor actor )
    {
        Stage stage = actor.getStage();
        for ( Iterator<Actor> i = stage.iterator(); i.hasNext();) {
            Actor other = i.next();
            
            if ( (other != actor) && (other.getX() == actor.getX()) && (other.getY() == actor.getY()) ) {
                i.remove();
            }
        }
    }

}
