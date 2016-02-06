/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Itchy;

/**
 * When clicked, this button launches the Editor for the current game. This is useful for adding to your game's title page if you want
 * people to edit scenes.
 */
public class EditorButton extends ButtonRole
{
    @Override
    protected void onClick()
    {
        Itchy.getGame().startEditor();
    }

}
