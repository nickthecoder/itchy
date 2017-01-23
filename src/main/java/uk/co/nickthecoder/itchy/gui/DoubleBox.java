/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.text.DecimalFormat;
import java.text.Format;

import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.Symbol;

public class DoubleBox extends EntryBox
{
    public Format format;

    public double minimumValue = -Double.MAX_VALUE;
    public double maximumValue = Double.MAX_VALUE;

    public DoubleBox(double value)
    {
        this(value, new DecimalFormat("#.####"));
    }

    public DoubleBox(double value, Format format)
    {
        super(format.format(value));
        this.format = format;
        this.boxWidth = 10;
    }

    public DoubleBox minimum(double from)
    {
        this.minimumValue = from;
        return this;
    }

    public DoubleBox maxium(double to)
    {
        this.maximumValue = to;
        return this;
    }

    public double getValue() throws Exception
    {
        if (!isValid()) {
            throw new Exception("Not valid");
        }
        return Double.parseDouble(this.getText());
    }

    public double getSafeValue()
    {
        try {
            return getValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected boolean setEntryText(String value)
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

    public void setValue(double value)
    {
        this.setEntryText(this.format.format(value));
    }

    public void adjust(double delta)
    {
        try {
            double newValue = Double.parseDouble(this.getText()) + delta;
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
            double number = Double.parseDouble(this.getText());
            return (number >= this.minimumValue) && (number <= this.maximumValue);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
        if (ke.symbol == Symbol.UP) {
            this.adjust(1);
            ke.stopPropagation();
        } else if (ke.symbol == Symbol.DOWN) {
            this.adjust(-1);
            ke.stopPropagation();
        } else if (ke.symbol == Symbol.PAGEUP) {
            this.adjust(10);
            ke.stopPropagation();
        } else if (ke.symbol == Symbol.PAGEDOWN) {
            this.adjust(-10);
            ke.stopPropagation();
        }
        super.onKeyDown(ke);
    }

    @Override
    public void onMouseDown(MouseButtonEvent mbe)
    {
        if (this.hasFocus) {

            /**
             * TODO Use Mousebutton modifiers
             * double amount = 1;
             * if (Itchy.isKeyDown(ScanCode.LSHIFT) || Itchy.isKeyDown(ScanCode.RSHIFT)) {
             * amount = 10;
             * } else if (Itchy.isKeyDown(ScanCode.LCTRL) ||
             * Itchy.isKeyDown(ScanCode.RCTRL)) {
             * amount = 0.1;
             * }
             */

            /*
             * TODO Use wheel events
             * if (mbe.button == MouseButtonEvent.BUTTON_WHEEL_UP) {
             * this.adjust(amount);
             * mbe.stopPropagation();
             * 
             * } else if (mbe.button == MouseButtonEvent.BUTTON_WHEEL_DOWN) {
             * this.adjust(-amount);
             * mbe.stopPropagation();
             * }
             */
        }

        super.onMouseDown(mbe);
    }

}
