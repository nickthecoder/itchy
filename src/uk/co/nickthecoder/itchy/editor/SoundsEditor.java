/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.JameRuntimeException;

public class SoundsEditor extends SubEditor
{

    private TextBox txtName;

    private FilenameComponent txtFilename;

    private SoundResource currentSoundResource;

    private SimpleTableModel tableModel;

    public SoundsEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Container createPage()
    {
        Container form = super.createPage();
        form.setFill(true, true);

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

        this.tableModel = this.createTableModel();
        this.table = new Table(this.tableModel, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                SoundsEditor.this.onEdit();
            }
        });

        this.table.sort(0);
        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        form.addChild(this.table);

        form.addChild(this.createListButtons());

        return form;
    }

    private SimpleTableModel createTableModel()
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

    private void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentSoundResource = (SoundResource) resource;

        this.txtName = new TextBox(this.currentSoundResource.getName());
        grid.addRow(new Label("Name"), this.txtName);

        this.txtFilename = new FilenameComponent(this.editor.resources,
                this.currentSoundResource.filename);
        grid.addRow(new Label("Filename"), this.txtFilename);
    }

    @Override
    protected void onOk()
    {
        boolean exists = this.editor.resources.fileExists(this.txtFilename.getText());
        if (!exists) {
            this.setMessage("Filename not found");
            return;
        }
        if (this.adding || (!this.txtName.getText().equals(this.currentSoundResource.getName()))) {
            if (this.editor.resources.getSoundResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }

        this.currentSoundResource.setName(this.txtName.getText());
        this.currentSoundResource.filename = this.txtFilename.getText();

        if (this.adding) {
            try {
                this.editor.resources.addSound(this.currentSoundResource);
                this.rebuildTable();
            } catch (JameRuntimeException e) {
                this.setMessage(e.getMessage());
            }
        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

        Itchy.getGame().hideWindow(this.editWindow);
    }

    @Override
    protected void remove( Object resource )
    {
        SoundResource soundResource = (SoundResource) resource;

        this.editor.resources.removeSound(soundResource.getName());
        this.rebuildTable();

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
        Itchy.getGame().showWindow(this.openDialog);
    }

    public void onAdd( File file )
    {
        if (file == null) {
            Itchy.getGame().hideWindow(this.openDialog);
        } else {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);
            try {
                this.currentSoundResource = new SoundResource(this.editor.resources, name, filename);
                this.currentSoundResource.getSound().play();
                this.adding = true;
                Itchy.getGame().hideWindow(this.openDialog);
                this.showDetails(this.currentSoundResource);
            } catch (JameException e) {
                this.openDialog.setMessage(e.getMessage());
                return;
            }
        }
    }

}
