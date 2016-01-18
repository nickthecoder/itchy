/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.GameInfo;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FolderComponent;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;

public class GameInfoEditor
{
    private Editor editor;

    public GameInfoEditor( Editor editor )
    {
        this.editor = editor;
    }

    public Container createPage()
    {
        GameInfo gameInfo = this.editor.resources.getGameInfo();


        PropertiesForm<GameInfo> form = new PropertiesForm<GameInfo>(gameInfo, gameInfo.getProperties());        

        FolderComponent folderComponent = new FolderComponent(this.editor.resources,this.editor.resources.getDirectory());
        folderComponent.setReadOnly(true);
        form.grid.addRow("Folder", folderComponent );

        form.autoUpdate = true;
        return form.createForm();
    }

}
