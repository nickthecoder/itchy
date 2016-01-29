/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.InputProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.KeysEnum;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

/**
 * The set of keys and joystick events which can be used as input for a particular task. For example, to fire a weapon,
 * you could define multiple keys, and multiple joystick buttons, all of which will cause the weapon to fire. Each
 * player can then choose which they find most comfortable.
 * 
 * You typically use the Editor, to define each of the Inputs, and then your game code just needs to find it, using
 * {@#find(String)} and then check {@#pressed} (typically in a tick method).
 * 
 * Note, only keyboard and mouse input is implemented at present, but joystick will be added later.
 */
public class Input implements NamedSubject<Input>, InputInterface
{
    protected static final List<Property<Input, ?>> properties = new ArrayList<Property<Input, ?>>();

    static {
        properties.add(new StringProperty<Input>("name"));
        properties.add(new InputProperty<Input>("keys").access("keysString"));
    }

    @Override
    public List<Property<Input, ?>> getProperties()
    {
        return properties;
    }

    private static int parseMouseButton(String str)
    {
        if (str.startsWith("M_")) {
            String rest = str.substring(2);

            if (rest.equals("LEFT")) {
                return MouseButtonEvent.BUTTON_LEFT;
            }
            if (rest.equals("RIGHT")) {
                return MouseButtonEvent.BUTTON_RIGHT;
            }
            if (rest.equals("MIDDLE")) {
                return MouseButtonEvent.BUTTON_MIDDLE;
            }
            if (rest.equals("WHEELUP")) {
                return MouseButtonEvent.BUTTON_WHEELUP;
            }
            if (rest.equals("WHEELDOWN")) {
                return MouseButtonEvent.BUTTON_WHEELDOWN;
            }
        }

        return -1;
    }

    /**
     * Parses a String with the following format :
     * [click+][ctrl+][shift+][meta+][alt+]KEY_NAME
     *
     * Where KEY_NAME is in KeysEnum, or M_LEFT, M_RIGHT, M_MIDDLE, M_WHEELUP, M_WHEELDOWN (for mouse buttons).
     * If "click" is specified, then the input trigers when the key/mouse is pressed, not continuously while the
     * key/button is held down.
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public static InputInterface parse(String str)
        throws Exception
    {
        AbstractInput result;

        String[] parts = str.split("\\+");

        String keyStr = parts[parts.length - 1];
        
        KeysEnum key = null;
        try {
            key = KeysEnum.valueOf(keyStr);
        } catch (Exception e) {
        }
        if (key == null) {
            int mouseButton = parseMouseButton(keyStr);
            if (mouseButton >= 0) {
                MouseInput mdi = new MouseInput();
                mdi.button = mouseButton;
                result = mdi;
            } else {
                throw new Exception("Expected a key or mouse button name, but found : " + keyStr);
            }
            
        } else {
            KeyInput keyInput = new KeyInput();
            keyInput.key = key;
            result = keyInput;
        }

        for (int i = 0; i < parts.length - 1; i++) {

            String part = parts[i];

            if (part.equals("click")) {
                result.click = true;
            } else if (part.equals("ctrl")) {
                result.ctrlModifier = true;
            } else if (part.equals("shift")) {
                result.shiftModifier = true;

            } else if (part.equals("meta")) {
                result.metaModifier = true;

            } else if (part.equals("alt")) {
                result.altModifier = true;

            } else {
                throw new Exception("Expected shift,meta, or alt, but found : " + part);
            }

        }

        return result;
    }

    private String name;

    private List<InputInterface> keys;

    private String asString = null;

    /**
     * Finds the Input from the current game's resources
     * 
     * @param name
     *            The name of the Input to search for.
     * @return The named input, or a dummy input if the named one was not found.
     */
    public static Input find(String name)
    {
        Input result = Itchy.getGame().resources.getInput(name);
        if (result == null) {
            System.err.println("Didn't find Input : " + name + " in " + Itchy.getGame().resources.getFilename());
            return new Input();
        }
        return result;
    }

    public Input()
    {
        this.asString = "";
        this.name = "";
        this.keys = new ArrayList<InputInterface>();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Builds a string representation of the set of keys for this input.
     * The format is comma separated list of : [ctrl+][shift+][meta+][alt+]KEY_NAME.
     * Used when saving the resources file.
     * 
     * @return The string representation of the set of keys for this input.
     */
    public String getKeysString()
    {
        if (this.asString == null) {
            StringBuffer buffer = new StringBuffer();
            for (InputInterface input : this.keys) {
                if (buffer.length() > 0) {
                    buffer.append(",");
                }
                buffer.append(input.toString());
            }
        }
        return this.asString;
    }

    /**
     * Parses a String representation of the input keys, replacing any existing input keys.
     * This is used when loading a resources file.
     * 
     * @param keys
     * @throws Exception
     */
    public void setKeysString(String keys)
        throws Exception
    {

        String[] parts = keys.split(",");
        this.keys.clear();
        for (String part : parts) {
            part = part.trim();
            if (! part.equals("")) {
                this.keys.add(Input.parse(part));
            }
        }

        this.asString = keys;
    }

    /**
     * @param ke
     * @return true iff the keyboard event matches one of this Input's key/mouse combinations.
     *         Use this from your game code's onKeyDown method.
     */
    @Override
    public boolean matches(KeyboardEvent ke)
    {
        for (InputInterface input : this.keys) {
            if (input.matches(ke)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param mbe
     * @return true iff the keyboard event matches one of this Input's key/mouse combinations.
     */
    @Override
    public boolean matches(MouseButtonEvent mbe)
    {
        for (InputInterface input : this.keys) {
            if (input.matches(mbe)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests each of this Input's possible triggers, and returns true if any of then are currently pressed.
     * 
     * @return true iff any of this Input's triggers are pressed.
     */
    @Override
    public boolean pressed()
    {
        for (InputInterface input : this.keys) {
            if (input.pressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "Keys : " + this.getKeysString();
    }

}
