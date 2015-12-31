/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;

public class Slider extends DragableContainer implements Layout
{
    public double from;

    public double to;

    public int steps = 20; // The number of steps to go from left to right when clicking.

    private double value;

    private ImageComponent knob;

    private int dragX;

    private final List<ComponentChangeListener> changeListeners;

    public Slider( double value )
    {
        setLayout(this);
        this.value = value;

        this.knob = new ImageComponent();
        this.knob.addStyle("knob");
        this.addChild(this.knob);

        this.changeListeners = new ArrayList<ComponentChangeListener>();

    }

    public Slider range( double from, double to )
    {
        this.type = "slider";
        this.from = from;
        this.to = to;
        return this;
    }

    public void addChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.remove(listener);
    }

    public Slider link( final IntegerBox link )
    {
        link.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                try {
                    setValue(link.getValue());
                } catch (Exception e) {
                    // Do nothing
                }
            }
        });

        this.addChangeListener(new ComponentChangeListener() {
            @Override
            public void changed()
            {
                link.setValue((int) Slider.this.getValue());
            }
        });

        return this;
    }

    public double getValue()
    {
        return this.value;
    }

    public void setValue( double value )
    {
        if (value != this.value) {
            this.value = value;

            if (this.value < this.from) {
                this.value = this.from;
            }
            if (this.value > this.to) {
                this.value = this.to;
            }

            forceLayout();

            for (ComponentChangeListener listener : this.changeListeners) {
                listener.changed();
            }
        }
    }

    public void adjust( double delta )
    {
        this.setValue(this.value + delta);
    }

    @Override
    public void calculateRequirements( PlainContainer container )
    {
    }

    private int getKnobRange()
    {
        return this.getWidth() - this.getPaddingLeft() - this.getPaddingRight() -
            this.knob.getNaturalWidth();
    }

    @Override
    public void layout( PlainContainer container )
    {
        int x = (int) ((this.value - this.from) / (this.to - this.from) * getKnobRange());

        this.knob.setPosition(
            this.getPaddingLeft() + x,
            this.getPaddingTop(),
            this.knob.getNaturalWidth(),
            this.knob.getNaturalHeight());
    }

    @Override
    public void onClick( MouseButtonEvent mbe )
    {
        if (mbe == null) {
            return;
        }
        if (mbe.x < this.knob.getX()) {
            adjust((-this.to - this.from) / this.steps);
        } else {
            adjust((this.to - this.from) / this.steps);
        }
    }

    @Override
    public boolean acceptDrag( MouseButtonEvent e )
    {

        if (this.knob.contains2(e)) {
            this.dragX = this.knob.getX();
            return true;
        }
        return false;
    }

    @Override
    public void drag( MouseEvent mme, int dx, int dy )
    {
        moveTo(this.dragX + dx);
    }

    @Override
    public void endDrag( MouseButtonEvent e, int dx, int dy )
    {
        moveTo(this.dragX + dx);
    }

    private void moveTo( int x )
    {
        double range = this.to - this.from;

        double value = ((double) x - this.getPaddingLeft()) / getKnobRange() * range + this.from;
        setValue(value);
    }
}
