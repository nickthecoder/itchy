/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class IntegerBox extends EntryBox<IntegerBox>
{

    public int minimumValue = Integer.MIN_VALUE;
    public int maximumValue = Integer.MAX_VALUE;

    public IntegerBox( int value )
    {
        this(value, 10);
    }

    public IntegerBox( int value, int width )
    {
        super(Integer.toString(value));
        this.addStyle("integerBox");
        this.boxWidth = width;
    }

    public IntegerBox minimum( int from )
    {
        this.minimumValue = from;
        return this;
    }

    public IntegerBox maxium( int to )
    {
        this.maximumValue = to;
        return this;
    }

    public IntegerBox range( int from, int to )
    {
        this.minimumValue = from;
        this.maximumValue = to;
        return this;
    }

    public int getSafeValue( int defaultValue )
    {
        try {
            return this.getValue();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getValue() throws Exception
    {
        if (!isValid()) {
            throw new Exception("Not valid");
        }
        return Integer.parseInt(this.getText());
    }

    @Override
    protected boolean setEntryText( String value )
    {
        if (value.equals("") || value.equals("-")) {
        } else {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return super.setEntryText(value);
    }

    public void setValue( int value )
    {
        this.setEntryText(Integer.toString(value));
    }

    public void adjust( int delta )
    {
        try {
            int newValue = this.getValue() + delta;
            if ((newValue >= this.minimumValue) && (newValue <= this.maximumValue)) {
                this.setValue(newValue);
            }
        } catch (Exception e) {
            // Do nothing
        }
    }

    public boolean isValid()
    {
        try {
            int number = Integer.parseInt(this.getText());
            return (number >= this.minimumValue) && (number <= this.maximumValue);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onKeyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.UP) {
            this.adjust(1);
            return true;
        } else if (ke.symbol == Keys.DOWN) {
            this.adjust(-1);
            return true;
        } else if (ke.symbol == Keys.PAGEUP) {
            this.adjust(10);
            return true;
        } else if (ke.symbol == Keys.PAGEDOWN) {
            this.adjust(-10);
            return true;
        }
        return super.onKeyDown(ke);
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent mbe )
    {
        if (this.hasFocus) {
            if (mbe.button == MouseButtonEvent.BUTTON_WHEELUP) {
                this.adjust(Itchy.isKeyDown(Keys.LSHIFT) ||
                    Itchy.isKeyDown(Keys.RSHIFT) ? 10 : 1);
                return true;

            } else if (mbe.button == MouseButtonEvent.BUTTON_WHEELDOWN) {
                this.adjust(Itchy.isKeyDown(Keys.LSHIFT) ||
                    Itchy.isKeyDown(Keys.RSHIFT) ? -10 : -1);
                return true;
            }
        }

        return super.onMouseDown(mbe);
    }

}
