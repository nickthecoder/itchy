/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

public interface StageListener
{
    /**
     * An Actor was added to the Stage.
     */
    public void onAdded( Stage stage, Actor actor );
    
    /**
     * An Actor was remove from the Stage.
     */
    public void onRemoved( Stage stage, Actor actor );
}
