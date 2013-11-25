/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.StringList;
import uk.co.nickthecoder.itchy.util.Util;

public class FontsEditor extends SubEditor<FontResource>
{

    public FontsEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Table createTable()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<String>(1);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);

        TableModel model = this.createTableModel();
        Table table = new Table(model, columns);
        return table;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String fontName : this.editor.resources.fontNames()) {
            FontResource fontResource = this.editor.resources.getFontResource(fontName);
            String[] attributeNames = { "name", "filename" };
            TableModelRow row = new ReflectionTableModelRow<FontResource>(fontResource,
                attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void remove( FontResource fontResource )
    {
        StringList usedBy = new StringList();

        for (String costumeName : this.editor.resources.costumeNames()) {
            CostumeResource cr = this.editor.resources.getCostumeResource(costumeName);
            Costume costume = cr.getCostume();
            for (String resourceName : costume.getFontNames()) {
                for (FontResource resource : costume.getFontChoices(resourceName)) {
                    if (resource == fontResource) {
                        usedBy.add(costumeName);
                    }
                }
            }
        }
        if (usedBy.isEmpty()) {
            if ( ! usedInScenes( fontResource ) ) { 
                this.editor.resources.removeFont(fontResource.getName());
            }
        } else {
            new MessageBox("Cannot Delete. Used by Costumes...", usedBy.toString()).show();
        }
    }
    
    private boolean usedInScenes( FontResource fontResource )
    {
        StringList list = new StringList();
        
        MessageBox messageBox = new MessageBox( "Checking All Scenes", "This may take a while" );
        messageBox.showNow();
        
        try {
            Resources resources = this.editor.resources;
            for ( String sceneName : resources.sceneNames()) {
                try {
                    Scene scene = resources.getScene(sceneName);
                    if (scene.uses(fontResource)) {
                        list.add( sceneName );
                    }
                } catch( Exception e) {
                    list.add( sceneName+ " (failed to load)");
                }
            }
            
        } finally {
            messageBox.hide();
        }
        
        if (!list.isEmpty()) {
            new MessageBox( "Cannot Delete. Used in scenes...", list.toString()).show();
        }
        
        return ! list.isEmpty();
    }

    @Override
    protected void onAdd()
    {
        this.openDialog = new FileOpenDialog() {
            @Override
            public void onChosen( File file )
            {
                FontsEditor.this.onAdd(file);
            }
        };

        this.openDialog.setDirectory(this.editor.resources.getDirectory());
        this.openDialog.show();
    }

    public void onAdd( File file )
    {
        this.openDialog.hide();

        if (file != null) {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);

            this.edit(new FontResource(this.editor.resources, name, filename), true);
        }
    }

    protected void onRename()
    {
        FilenameComponent filename = (FilenameComponent) this.form.getComponent("file");

        if (!this.editor.resources.renameFile(this.currentResource.getFilename(), filename.getText())) {
            this.setMessage("Rename failed");
        } else {
            this.currentResource.setFile(filename.getValue());
            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

    }

    @Override
    protected void update() throws MessageException
    {
        FilenameComponent filename = (FilenameComponent) this.form.getComponent("file");
        TextBox name = (TextBox) this.form.getComponent("name");

        boolean exists = this.editor.resources.fileExists(filename.getText());
        if (!exists) {
            throw new MessageException("Filename not found");
        }
        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {
            if (this.editor.resources.getFontResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addFont(this.currentResource);
        }
    }

    @Override
    protected List<AbstractProperty<FontResource, ?>> getProperties()
    {
        return FontResource.properties;
    }

}
