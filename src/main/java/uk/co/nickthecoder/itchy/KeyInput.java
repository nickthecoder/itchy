/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.ScanCode;

public class KeyInput extends AbstractInput
{
    // TODO Should we have a choice of scancode OR symbol?
    public ScanCode key;

    public KeyInput()
    {
    }

    public KeyInput(ScanCode key)
    {
        this.key = key;
    }

    public boolean matches(KeyboardEvent ke)
    {
        if (ke.pressed && (ke.scanCode == this.key)) {
            return super.matches(ke);
        }
        return false;
    }

    public boolean pressed()
    {

        if (Itchy.isKeyDown(this.key.value)) {
            if ( (click && previouslyUp) || (!click) ) {
                previouslyUp = false;
                return super.pressed();
            }
        } else {
            previouslyUp = true;
        }
        return false;
    }
    

    @Override
    public String toString()
    {
        return super.toString() + this.key.name();
    }
}
