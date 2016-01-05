/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class GuiButton extends ClickableContainer
{
    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public GuiButton()
    {
        super();
        this.type = "button";
        this.focusable = true;
    }

    public GuiButton( String text )
    {
        this(new Label(text));
    }

    public GuiButton( AbstractComponent child )
    {
        this();

        this.addChild(child);
        this.setXAlignment(0.5f);
        this.setYAlignment(0.5f);
    }

    @Override
    public void onClick( MouseButtonEvent e )
    {
        this.focus();

        for (ActionListener actionListener : this.actionListeners) {
            actionListener.action();
        }
    }

    public void addActionListener( ActionListener actionListener )
    {
        this.actionListeners.add(actionListener);
    }
}
