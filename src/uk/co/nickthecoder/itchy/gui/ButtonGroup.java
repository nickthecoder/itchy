/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

public class ButtonGroup
{
    public List<ToggleButton> buttons = new ArrayList<ToggleButton>();

    public ToggleButton defaultButton;

    public void add( ToggleButton button )
    {
        assert (button.buttonGroup == null);
        button.buttonGroup = this;
        this.buttons.add(button);
    }

    public void select( ToggleButton selected )
    {
        for (ToggleButton button : this.buttons) {
            button.setState(button == selected);
        }
    }

}
