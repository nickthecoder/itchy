/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.jame.Keys;
import uk.co.nickthecoder.jame.ModifierKey;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public abstract class RootContainer extends Container
{
    protected Stylesheet stylesheet;

    protected static Component focus;

    public RootContainer()
    {
        super();
        this.type = "root";
        this.stylesheet = Itchy.getGame().getStylesheet();
    }

    public void style( Component component )
    {
        if (this.stylesheet != null) {
            this.stylesheet.style(component);
        }
    }

    public void setStylesheet( Stylesheet stylesheet )
    {
        this.stylesheet = stylesheet;
        this.reStyle();
    }

    public Stylesheet getStylesheet()
    {
        return this.stylesheet;
    }

    @Override
    public RootContainer getRoot()
    {
        return this;
    }

    @Override
    public boolean mouseDown( MouseButtonEvent mbe )
    {
        if (super.mouseDown(mbe)) {
            return true;
        }
        this.setFocus(null);
        return false;
    }

    public boolean keyDown( KeyboardEvent ke )
    {
        if (ke.symbol == Keys.TAB) {

            if (ke.modifier(ModifierKey.SHIFT)) {

                if (RootContainer.focus == null) {
                    this.previousFocus(null, this);
                } else {
                    RootContainer.focus.parent.previousFocus(RootContainer.focus,
                        RootContainer.focus);
                }

            } else {

                if (RootContainer.focus == null) {
                    this.nextFocus(null, this);
                } else {
                    RootContainer.focus.parent.nextFocus(RootContainer.focus, RootContainer.focus);
                }

            }
            return true;
        }

        return false;
    }

    @Override
    public void focus()
    {
        this.nextFocus(null, this);
    }

    public void setFocus( Component component )
    {
        Itchy.getGame().setFocus(component);

        if (RootContainer.focus == component) {
            return;
        }
        if (RootContainer.focus != null) {
            RootContainer.focus.hasFocus = false;
            RootContainer.focus.removeStyle("focus");
            RootContainer.focus.getRoot().removeStyle("focus");
            RootContainer.focus.onFocus(false);
        }
        RootContainer.focus = component;

        if (RootContainer.focus != null) {
            RootContainer.focus.hasFocus = true;
            RootContainer.focus.addStyle("focus");
            this.addStyle("focus");
            RootContainer.focus.onFocus(true);

            // Ensure that the newly focused component is visible, scrollable will scroll,
            // and notebooks will select the appropriate tab.
            Container parent = component.getParent();
            while (parent != null) {
                parent.ensureVisible(component);
                parent = parent.getParent();
            }
        }

    }

    public abstract void captureMouse( Component component );

    public abstract void releaseMouse( Component component );

}
