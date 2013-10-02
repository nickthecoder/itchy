/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.text.DecimalFormat;
import java.text.Format;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.event.Event;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class DoubleBox extends EntryBox<DoubleBox>
{
    public Format format;

    public DoubleBox( double value )
    {
        this(value, new DecimalFormat("#.####"));
    }

    public DoubleBox( double value, Format format )
    {
        super(format.format(value));
        this.format = format;
        this.boxWidth = 10;
    }

    public double getValue()
    {
        return Double.parseDouble(this.getText());
    }

    @Override
    protected boolean setEntryText( String value )
    {
        if (value.equals("") || value.equals("-")) {
        } else {
            try {
                Double.parseDouble(value + "0");
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return super.setEntryText(value);
    }

    public void setValue( double value )
    {
        this.setEntryText(this.format.format(value));
    }

    public void adjust( double delta )
    {
        this.setValue(this.getValue() + delta);
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
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if (this.hasFocus) {

            double amount = 1;
            if (Itchy.isKeyDown(Keys.LSHIFT) || Itchy.isKeyDown(Keys.RSHIFT)) {
                amount = 10;
            } else if (Itchy.isKeyDown(Keys.LCTRL) ||
                    Itchy.isKeyDown(Keys.RCTRL)) {
                amount = 0.1;
            }

            if (mbe.button == Event.BUTTON_WHEELUP) {
                this.adjust(amount);
                return true;

            } else if (mbe.button == Event.BUTTON_WHEELDOWN) {
                this.adjust(-amount);
                return true;
            }
        }

        return super.mouseDown(mbe);
    }

}
