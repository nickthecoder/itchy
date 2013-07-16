/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.MessageDialog;
import uk.co.nickthecoder.itchy.gui.TextBox;

public class FilenameComponent extends Container
{
    private final Resources resources;

    private final TextBox textBox;

    private FileOpenDialog openDialog;

    private String initialFilename;

    public FilenameComponent( Resources resources, String filename )
    {
        this.resources = resources;

        this.textBox = new TextBox(filename);
        this.addChild(this.textBox);

        Button pick = new Button("...");
        pick.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FilenameComponent.this.pickFilename();
            }
        });
        this.addChild(pick);

        Button rename = new Button("Rename");
        rename.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FilenameComponent.this.onRename();
            }
        });
        this.addChild(rename);

        this.setText(filename);
    }

    public final void setText( String value )
    {
        this.textBox.setText(value);
        this.initialFilename = value;
    }

    public String getText()
    {
        return this.textBox.getText();
    }

    private void pickFilename()
    {
        this.openDialog = new FileOpenDialog() {
            @Override
            public void onChosen( File file )
            {
                FilenameComponent.this.onPickFilename(file);
            }
        };
        this.openDialog.setDirectory(this.resources.getDirectory());
        Itchy.singleton.showWindow(this.openDialog);
    }

    private void onPickFilename( File file )
    {
        if (file == null) {
            Itchy.singleton.hideWindow(this.openDialog);
        } else {
            String filename = this.resources.makeRelativeFilename(file);
            this.textBox.setText(filename);
            Itchy.singleton.hideWindow(this.openDialog);
        }
    }

    protected void onRename()
    {
        if (!this.resources.rename(this.initialFilename, this.textBox.getText())) {
            new MessageDialog("Error", "Rename failed").show();
        }

    }

}
