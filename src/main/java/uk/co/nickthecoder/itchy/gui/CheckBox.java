/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class CheckBox extends ClickableContainer
{
    private boolean value;

    private final ImageComponent image;

    private final List<ComponentChangeListener> changeListeners;

    private final List<ComponentValidator> validators;

    public CheckBox( boolean value )
    {
        super();
        this.changeListeners = new ArrayList<ComponentChangeListener>();
        this.validators = new ArrayList<ComponentValidator>();

        this.image = new ImageComponent();
        this.addChild(this.image);

        this.setValue(value);
        this.type = "checkbox";

    }

    public CheckBox()
    {
        this(false);
    }

    public void addChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.add(listener);
    }

    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.changeListeners.remove(listener);
    }

    public void addValidator( ComponentValidator validator )
    {
        this.validators.add(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.validators.remove(validator);
    }
    
    public boolean getValue()
    {
        return this.value;
    }

    public final void setValue( boolean value )
    {
        if (this.value != value) {
            this.value = value;
            if (this.value) {
                this.addStyle("checked");
            } else {
                this.removeStyle("checked");
            }
            this.invalidate();

            this.removeStyle("error");
            for (ComponentValidator validator : this.validators) {
                if ( ! validator.isValid() ) {
                    this.addStyle("error");
                }
            }
            for (ComponentChangeListener listener : this.changeListeners) {
                listener.changed();
            }

        }
        this.image.setVisible(value);
    }

    @Override
    public int getNaturalWidth()
    {
        return this.image.getRequiredHeight() + this.image.getMarginLeft() +
            this.image.getMarginRight() + this.getPaddingLeft() + this.getPaddingRight();
    }

    @Override
    public int getNaturalHeight()
    {
        return this.image.getRequiredWidth() + this.image.getMarginTop() +
            this.image.getMarginBottom() + this.getPaddingTop() + this.getPaddingBottom();
    }

    @Override
    public void onClick( MouseButtonEvent mbe )
    {
        this.setValue(!this.getValue());
    }
}
