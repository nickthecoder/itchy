/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;

public class SoundsEditor extends SubEditor<SoundResource>
{

    public SoundsEditor( Editor editor )
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

        TableModelColumn play = new TableModelColumn("Play", 1, 100) {
            @Override
            public Component createCell( final TableModelRow row )
            {
                Container container = new Container();

                Button button = new Button("Play");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void action()
                    {
                        ReflectionTableModelRow<?> rrow = (ReflectionTableModelRow<?>) row;
                        ((SoundResource) rrow.getData()).getSound().play();
                    };
                });
                container.addChild(button);
                return container;

            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(play);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String soundName : this.editor.resources.soundNames()) {
            SoundResource soundResource = this.editor.resources.getSoundResource(soundName);
            String[] attributeNames = { "name", "filename" };
            TableModelRow row = new ReflectionTableModelRow<SoundResource>(soundResource,
                attributeNames);
            model.addRow(row);
        }
        return model;
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
            if (this.editor.resources.getSoundResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addSound(this.currentResource);
        }
    }

    @Override
    protected void remove( SoundResource soundResource )
    {
        this.editor.resources.removeSound(soundResource.getName());
    }

    @Override
    protected void onAdd()
    {
        this.openDialog = new FileOpenDialog() {
            @Override
            public void onChosen( File file )
            {
                SoundsEditor.this.onAdd(file);
            }
        };
        this.openDialog.setDirectory(this.editor.resources.getDirectory());
        this.openDialog.show();
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
                this.edit(new SoundResource(this.editor.resources, name, filename), true);

            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

    @Override
    protected List<AbstractProperty<SoundResource, ?>> getProperties()
    {
        return SoundResource.properties;
    }

}
