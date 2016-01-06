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
 * You typically use the Editor, to define each of the Inputs, and then you game code just needs to find it, using {@#find(String)}
 * and then check the {@#pressed} (typically in a tick method).
 * 
 * Note, only keyboard input is implemented at present, but other input devices such as mouse and joystick will be added later.
 */
public class Input
{
    private List<KeyInput> keys;

    private String asString = null;

    /**
     * Finds the Input from the current game's resources
     * @param name The name of the Input to search for.
     * @return The named input, or a dummy input if the named one was not found.
     */
    public static Input find( String name )
    {
        Input result = Itchy.getGame().resources.getInput(name);
        if (result == null) {
            System.err.println( "Didn't find Input : " + name + " in " + Itchy.getGame().resources.getFilename() );
            try {
                throw new Exception( "Foo" );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Input();
        }
        return result;
    }

    public Input()
    {
        this.keys = new ArrayList<KeyInput>();
    }

    /**
     * Builds a string representation of the set of keys for this input.
     * The format is comma separated list of : [ctrl+][shift+][meta+][alt+]KEY_NAME.
     * Used when saving the resources file.
     * @return The string representation of the set of keys for this input.
     */
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

    /**
     * Parses a String representation of the input keys, replacing any existing input keys.
     * This is used when loading a resources file. 
     * @param keys
     * @throws Exception
     */
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

    /**
     * @param ke
     * @return true iff the keyboard event matches one of this Input's key combinations.
     * Use this from your game code's onKeyDown method.
     */
    public boolean matches( KeyboardEvent ke )
    {
        for (KeyInput keyInput : this.keys) {
            if (keyInput.matches(ke)) {
                return true;
            }
        }
        return false;        
    }
    
    /**
     * Tests each of this Input's possible triggers, and returns true if any of then are currently pressed.
     * @return true iff any of this Input's triggers are pressed.
     */
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
