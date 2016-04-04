/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

/**
 * The scene designer can move actors up and down the z-order, but only if the stage they are on can perform such actions.
 * This interface is here so that SceneDeisgner doesn't lock you into using a specific implementation of a Z-order stage.
 */
public interface OrderedStage extends Stage
{

    public void addBottom( Actor actor );

    public void addTop( Actor actor );

    public void addBelow( Actor actor, Actor other );

    public void addAbove( Actor actor, Actor other );

    public void zOrderUp( Actor actor );

    public void zOrderDown( Actor actor );

}
