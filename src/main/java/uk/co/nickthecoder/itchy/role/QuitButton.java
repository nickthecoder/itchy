/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Itchy;

/**
 * When clicked, the game will end.
 */
public class QuitButton extends ButtonRole
{
    @Override
    protected void onClick()
    {
        super.onClick();
        Itchy.getGame().end();
    }

}