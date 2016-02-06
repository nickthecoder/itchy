/*
 * Copyright (c) 2016 Nick Robinson All rights reserved.
 * This program and the accompanying materials are made
 * available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available
 * at http://www.gnu.org/licenses/gpl.html
 */
package uk.co.nickthecoder.itchy.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import uk.co.nickthecoder.itchy.Resources;

/**
 * TODO There is no "Open Folder" dialog, similar to FileOpenDialog, so there is no "..." button implemented.
 */
public class FolderComponent extends PlainContainer
{
    private final Resources resources;

    private final TextBox textBox;

    private final Button openButton;
    
    /**
     * True if the folder must exist - the textbox will have the "error" style if this is set, and the file does not
     * exist.
     */
    public boolean mustExist = true;

    public FolderComponent(Resources resources, File file)
    {
        this(resources, file.getPath());
    }

    public FolderComponent(Resources resources, String filename)
    {
        this.type = "filename";
        this.addStyle("combo");

        this.resources = resources;

        this.textBox = new TextBox(filename);
        this.textBox.addChangeListener(new ComponentChangeListener()
        {

            @Override
            public void changed()
            {
                FolderComponent.this.onChanged();
            }

        });
        this.addChild(this.textBox);

        this.openButton = new Button( "Open" );
        this.openButton.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                try {
                    Desktop.getDesktop().open(new File(textBox.getText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }                
            }
        });
        this.addChild(this.openButton);
        
        this.setText(filename);
    }

    public final void setText(String value)
    {
        this.textBox.setText(value);
        onChanged();
    }

    public String getText()
    {
        return this.textBox.getText();
    }

    public File getValue()
    {
        return new File(this.textBox.getText());
    }

    public void setReadOnly( boolean value)
    {
        this.textBox.setReadOnly(value);
    }
    
    public boolean getReadOnly()
    {
        return this.textBox.getReadOnly();
    }
    
    public void onChanged()
    {
        if (this.mustExist) {
            this.textBox.addStyle("error", ! isValid() );
        }
    }
    
    public boolean isValid()
    {
        File file = this.resources.resolveFile( new File(this.textBox.getText()));
        return file.exists() && file.isDirectory();
    }

    public void addChangeListener(ComponentChangeListener listener)
    {
        this.textBox.addChangeListener(listener);
    }

    public void removeChangeListener(ComponentChangeListener listener)
    {
        this.textBox.removeChangeListener(listener);
    }

}
