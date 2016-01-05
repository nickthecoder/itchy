/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class ToggleButton extends GuiButton
{
    protected boolean down;

    ButtonGroup buttonGroup;

    public ToggleButton( AbstractComponent component )
    {
        super(component);
        this.addStyle("toggle");
    }

    public ToggleButton( String label )
    {
        this(new Label(label));
    }

    @Override
    public void onClick( MouseButtonEvent e )
    {
        if (this.getState() && (this.buttonGroup != null) &&
            (this.buttonGroup.defaultButton != null) &&
            (this.buttonGroup.defaultButton != this)) {
            this.buttonGroup.defaultButton.onClick(e);
        } else {
            this.setState(!this.getState());
            super.onClick(e);
        }
    }

    public boolean getState()
    {
        return this.down;
    }

    public void setState( boolean value )
    {
        if (this.down != value) {
            this.down = value;

            if (this.down) {
                this.addStyle("down");
            } else {
                this.removeStyle("down");
            }
            if (this.down && (this.buttonGroup != null)) {
                this.buttonGroup.select(this);
            }
        }
    }

}
