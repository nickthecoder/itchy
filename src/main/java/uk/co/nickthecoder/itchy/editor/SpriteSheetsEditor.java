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
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.SpriteSheet;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.util.StringList;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class SpriteSheetsEditor extends SubEditor<SpriteSheet>
{
    public SpriteSheetsEditor( Editor editor )
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

        TableModelColumn image = new TableModelColumn("Image", 2, 100) {
            @Override
            public AbstractComponent createCell( TableModelRow row )
            {
                return new ImageComponent((Surface) (row.getData(this.index)));
            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(image);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    protected SimpleTableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String spriteSheetName : this.editor.resources.spriteSheetNames()) {
            SpriteSheet spriteSheet = this.editor.resources.getSpriteSheet(spriteSheetName);
            String[] attributeNames = { "name", "filename", "thumbnail" };
            TableModelRow row = new ReflectionTableModelRow<SpriteSheet>(spriteSheet, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected Component createForm()
    {
        super.createForm();

        return this.form.container;
    }

    @Override
    protected void update() throws MessageException
    {
        FilenameComponent filenameComponent = (FilenameComponent) this.form.getComponent("file");
        File file = filenameComponent.getValue();
        TextBox name = (TextBox) this.form.getComponent("name");

        boolean exists = this.editor.resources.fileExists(file.getPath());
        if (!exists) {
            throw new MessageException("File not found");
        }

        if (!this.editor.resources.fileIsWithin(file)) {
            File newFile = new File(getImageDirectory(), file.getName());
            if (newFile.exists()) {
                throw new MessageException("File is outside of this game's resource directory.");
            }
            try {
                Util.copyFile(this.editor.resources.resolveFile(file), newFile);
                filenameComponent.setText(this.editor.resources.makeRelativeFilename(newFile));
            } catch (Exception e) {
                throw new MessageException("Failed to copy image into the resources directory");
            }
        }

        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {
            if (this.editor.resources.getSpriteSheet(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addSpriteSheet(this.currentResource);
        }
    }

    @Override
    protected void remove( SpriteSheet spriteSheet )
    {
        StringList usedBy = new StringList();

        for (PoseResource poseResource : spriteSheet.getSprites()) {
        
            for (String costumeName : this.editor.resources.costumeNames()) {
                CostumeResource cr = this.editor.resources.getCostumeResource(costumeName);
                Costume costume = cr.getCostume();
                for (String resourceName : costume.getPoseNames()) {
                    for (PoseResource resource : costume.getPoseChoices(resourceName)) {
                        if (resource == poseResource) {
                            usedBy.add(costumeName);
                        }
                    }
                }
            }
            
        }
        
        if (usedBy.isEmpty()) {
            this.editor.resources.removeSpriteSheet(spriteSheet.getName());
        } else {
            new MessageBox("Cannot Delete. Used by Costumes...", usedBy.toString()).show();
        }
    }

    @Override
    protected void onAdd()
    {
        this.openDialog = new FileOpenDialog() {
            @Override
            public void onChosen( File file )
            {
                SpriteSheetsEditor.this.onAdd(file);
            }
        };
        this.openDialog.setDirectory(getImageDirectory());
        this.openDialog.show();
    }

    public File getImageDirectory()
    {
        File dir = this.editor.resources.getDirectory();
        File imageDir = new File(dir, "images");
        if (imageDir.exists()) {
            return imageDir;
        } else {
            return dir;
        }
    }

    public void onAdd( File file )
    {
        if (file == null) {
            this.openDialog.hide();

        } else {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);
            try {
                this.openDialog.hide();
                SpriteSheet spriteSheet = new SpriteSheet(this.getResources(), name);
                spriteSheet.setFilename( filename );
                this.edit(spriteSheet, true);

            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

    @Override
    protected List<Property<SpriteSheet, ?>> getProperties()
    {
        return this.currentResource.getProperties();
    }

}
