/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;

/**
 * A top-level Container, that has the appearance of a Window. It is composed of a title bar and a client area. The appearance of the
 * window, such as the size and colour of the title bar and borders are derived from the Game's Stylesheet.
 * 
 */
public class Window extends RootContainer
{
    public PlainContainer clientArea;

    public DragableContainer titleBar;

    public Label title;

    public Window( String titleString )
    {
        this.modal = true;

        this.setType("window");
        this.setLayout(new VerticalLayout());
        this.setFill(true, true);
        this.setMaximumWidth(Itchy.getGame().getWidth());
        this.setMaximumHeight(Itchy.getGame().getHeight());

        this.titleBar = new DragableContainer() {

            @Override
            public boolean beginDrag( MouseButtonEvent e )
            {
                return true;
            }

            @Override
            public void drag( MouseEvent event, int dx, int dy )
            {
                Window.this.setPosition(Window.this.x + dx, Window.this.y + dy, Window.this.width, Window.this.height);
            }

        };
        this.titleBar.setType("titleBar");

        this.title = new Label(titleString);
        this.titleBar.addChild(this.title);

        this.clientArea = new PlainContainer();
        this.clientArea.setType("clientArea");

        this.addChild(this.titleBar);
        this.addChild(this.clientArea);
        this.draggable = true;
    }

    @Override
    public void ensureLayedOut()
    {
        this.setPosition(this.x, this.y, this.getRequiredWidth(), this.getRequiredHeight());
        super.ensureLayedOut();
    }

}
