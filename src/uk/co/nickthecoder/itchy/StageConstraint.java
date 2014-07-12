/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

/**
 * When designing a scene, sometimes it is important to contrain where an actor can be positioned.
 * For example, in a grid based game, the actors can only be placed within one square of the grid,
 * and not half way between two.
 */

public interface StageConstraint
{
    public double constrainX( double requestedX, double requestedY );
    public double constrainY( double requestedX, double requestedY );
}
