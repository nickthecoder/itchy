/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.GameInfo;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.util.AbstractProperty;

public class GameInfoEditor
{
    private Editor editor;

    
    public GameInfoEditor( Editor editor )
    {
        this.editor = editor;
    }

    public Container createPage()
    {
        Container page = new Container();
        page.setLayout(new VerticalLayout());
        page.setFill(true, false);

        Container form = new Container();
        GridLayout grid = new GridLayout(form, 2);
        form.setLayout(grid);

        GameInfo gameInfo = this.editor.game.resources.gameInfo;

        for (AbstractProperty<GameInfo, ?> property : gameInfo.getProperties()) {
            try {
                Component component = property.createComponent(gameInfo, true);
                grid.addRow(property.label, component);
            } catch (Exception e) {
            }
        }

        page.addChild(form);

        return page;
    }

}
