/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jame.event.KeyboardEvent;

/**
 * The set of keys and joystick events which can be used as input for a particular task. For example, to fire a weapon, you could define
 * multiple keys, and multiple joystick buttons, all of which will cause the weapon to fire. Each player can then choose which they find
 * most comfortable.
 * 
 * You typically use the Editor, to define each of the Inputs, and then you game code just needs to find it (typically in a constructor),
 * and then check the Input.pressed (typically in a tick method).
 * 
 * Note, only keyboard input i implemented at present.
 * 
 */
public class Input
{
    private List<KeyInput> keys;

    private String asString = null;

    public static Input find( String name )
    {
        Input result = Itchy.getGame().resources.getInput(name);
        if (result == null) {
            System.err.println( "Didn't find Input : " + name + " in " + Itchy.getGame().resources.getFilename() );
            return new Input();
        }
        return result;
    }

    public Input()
    {
        this.keys = new ArrayList<KeyInput>();
    }

    public String getKeys()
    {
        if (this.asString == null) {
            StringBuffer buffer = new StringBuffer();
            for (KeyInput ki : this.keys) {
                if (buffer.length() > 0) {
                    buffer.append(",");
                }
                buffer.append(ki.toString());
            }
        }
        return this.asString;
    }

    public void parseKeys( String keys )
        throws Exception
    {

        String[] parts = keys.split(",");
        this.keys.clear();
        for (String part : parts) {
            this.keys.add(KeyInput.parseKeyInput(part));
        }

        this.asString = keys;
    }

    public boolean matches( KeyboardEvent ke )
    {
        for (KeyInput keyInput : this.keys) {
            if (keyInput.matches(ke)) {
                return true;
            }
        }
        return false;        
    }
    
    public boolean pressed()
    {
        for (KeyInput keyInput : this.keys) {
            if (keyInput.pressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Keys : " + this.getKeys();
    }

}
