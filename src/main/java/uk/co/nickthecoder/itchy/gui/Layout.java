/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

public interface Layout
{

    /**
     * Calculates the required width and height of the container based on the required widths and heights of its children, plus any margins
     * and spacing.
     */
    public void calculateRequirements( PlainContainer container );

    /**
     * Calculates the position and sizes of a Containers children. This can make use of the containers actual width and height.
     */
    public void layout( PlainContainer container );

}
