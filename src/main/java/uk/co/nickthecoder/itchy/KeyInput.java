/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Key;

public class KeyInput extends AbstractInput
{

    public Key key;

    public KeyInput()
    {
    }

    public KeyInput(Key key)
    {
        this.key = key;
    }

    public boolean matches(KeyboardEvent ke)
    {
        if (ke.isPressed() && (ke.symbol == this.key.value)) {
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
