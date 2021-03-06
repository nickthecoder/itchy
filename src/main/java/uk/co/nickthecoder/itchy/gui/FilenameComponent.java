/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.io.File;

import uk.co.nickthecoder.itchy.Resources;

public class FilenameComponent extends PlainContainer
{    
    private final Resources resources;

    private final TextBox textBox;

    private FileOpenDialog openDialog;

    /**
     * Used to hold the filename before it was changed, so that the rename button knows what to rename from.
     */
    private String preRename;

    public FilenameComponent( Resources resources, File file )
    {
        this(resources, file == null ? "" : file.getPath());
    }

    public FilenameComponent( Resources resources, String filename )
    {
        this.type = "filename";
        this.addStyle("combo");

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
        this.preRename = value;
    }

    public String getText()
    {
        return this.textBox.getText();
    }

    public File getValue()
    {
        return new File(this.textBox.getText());
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
        this.openDialog.show();
    }

    private void onPickFilename( File file )
    {
        if (file == null) {
            this.openDialog.hide();
        } else {
            String filename = this.resources.makeRelativeFilename(file);
            this.textBox.setText(filename);
            this.openDialog.hide();
        }
    }

    protected void onRename()
    {
        if (!this.resources.renameFile(this.preRename, this.textBox.getText())) {
            new MessageDialog("Error", "Rename failed").show();
        } else {
            this.preRename = this.textBox.getText();
        }
    }

    public void addChangeListener( ComponentChangeListener listener )
    {
        this.textBox.addChangeListener(listener);
    }

    public void removeChangeListener( ComponentChangeListener listener )
    {
        this.textBox.removeChangeListener(listener);
    }


    public void addValidator( ComponentValidator validator )
    {
        this.textBox.addValidator(validator);
    }

    public void removeValidator( ComponentValidator validator )
    {
        this.textBox.removeValidator(validator);
    }

}
