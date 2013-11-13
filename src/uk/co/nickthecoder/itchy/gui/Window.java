/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

/**
 * A top-level Container, that has the appearance of a Window.
 * It is composed of a title bar and a client area.
 * The appearance of the window, such as the size and colour of the title bar and borders
 * are derived from the Game's Stylesheet.
 *
 */
public class Window extends RootContainer
{
    public Container clientArea;

    public Container titleBar;

    public Label title;

    public Window( String titleString )
    {
        this.modal = true;

        this.setType("window");
        this.setLayout(new VerticalLayout());
        this.setFill(true, true);

        this.titleBar = new Container();
        this.titleBar.setType("titleBar");

        this.title = new Label(titleString);
        this.titleBar.addChild(this.title);

        this.clientArea = new Container();
        this.clientArea.setType("clientArea");

        this.addChild(this.titleBar);
        this.addChild(this.clientArea);
    }

    @Override
    public void ensureLayedOut()
    {
        this.setPosition(0, 0, this.getRequiredWidth(), this.getRequiredHeight());
        super.ensureLayedOut();
    }
    
}
