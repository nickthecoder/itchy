/*******************************************************************************
 * Copyright (c) 2014 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.event.KeysEnum;

public class KeyInput
{

    public KeysEnum key;

    public boolean ctrlModifier;
    public boolean shiftModifier;
    public boolean metaModifier;
    public boolean altModifier;
    public boolean superModifier;

    public static KeyInput parseKeyInput( String str )
        throws Exception
    {
        KeyInput result = new KeyInput();

        String[] parts = str.split("\\+");
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.equals("ctrl")) {
                result.ctrlModifier = true;
            } else if (part.equals("shift")) {
                result.shiftModifier = true;

            } else if (part.equals("meta")) {
                result.metaModifier = true;

            } else if (part.equals("alt")) {
                result.altModifier = true;

            } else if (part.equals("super")) {
                result.superModifier = true;
            } else {
                throw new Exception("Expected shift,meta,alt or super, but found : " + part);
            }

        }
        String keyStr = parts[parts.length - 1];
        KeysEnum key = KeysEnum.valueOf(keyStr);
        if (key == null) {
            throw new Exception("Expected a key name, but found : " + keyStr);
        }
        result.key = key;

        return result;
    }

    public KeyInput()
    {
    }

    public KeyInput( KeysEnum key )
    {
        this.key = key;
    }

    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        if (this.shiftModifier) {
            buffer.append("shift+");
        }
        if (this.ctrlModifier) {
            buffer.append("ctrl+");
        }
        if (this.metaModifier) {
            buffer.append("meta+");
        }
        if (this.altModifier) {
            buffer.append("alt+");
        }
        if (this.superModifier) {
            buffer.append("super+");
        }

        buffer.append(this.key.name());

        return buffer.toString();
    }
    
    public boolean pressed()
    {
        if ( ! Itchy.isKeyDown(this.key.value) ) {
            return false;
        }
        
        if (this.ctrlModifier && ! Itchy.isCtrlDown()) {
            return false;
        }
        if (this.shiftModifier && ! Itchy.isShiftDown()) {
            return false;
        }
        if (this.altModifier && ! Itchy.isAltDown()) {
            return false;
        }
        if (this.superModifier && ! Itchy.isSuperDown()) {
            return false;
        }
        if (this.metaModifier && ! Itchy.isMetaDown()) {
            return false;
        }

        return true;
    }
}
