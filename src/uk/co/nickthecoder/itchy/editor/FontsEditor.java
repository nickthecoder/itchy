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

import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.util.Util;

public class FontsEditor extends SubEditor
{

    private TextBox txtName;

    private TextBox txtFilename;

    private FontResource currentFontResource;

    public FontsEditor( Editor editor )
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

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);

        TableModel model = this.createTableModel();
        this.table = new Table(model, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                FontsEditor.this.onEdit();
            }
        });

        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        this.table.sort(0);

        form.addChild(this.table);
        form.addChild(this.createListButtons());

        return form;
    }

    private TableModel createTableModel()
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

    private void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentFontResource = (FontResource) resource;

        this.txtName = new TextBox(this.currentFontResource.getName());
        grid.addRow(new Label("Name"), this.txtName);

        Container filenameContainer = new Container();
        this.txtFilename = new TextBox(this.currentFontResource.filename);
        filenameContainer.addChild(this.txtFilename);

        Button rename = new Button("Rename");
        rename.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FontsEditor.this.onRename();
            }
        });
        filenameContainer.addChild(rename);
        grid.addRow(new Label("Filename"), filenameContainer);

    }

    @Override
    protected void onOk()
    {
        boolean exists = this.editor.resources.fileExists(this.txtFilename.getText());
        if (!exists) {
            this.setMessage("Filename not found");
            return;
        }
        if (this.adding || (!this.txtName.getText().equals(this.currentFontResource.getName()))) {
            if (this.editor.resources.getFontResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }

        this.currentFontResource.rename(this.txtName.getText());
        this.currentFontResource.filename = this.txtFilename.getText();

        if (this.adding) {
            this.editor.resources.addFont(this.currentFontResource);
            this.rebuildTable();
        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }
        Itchy.singleton.getGame().hideWindow(this.editWindow);

    }

    @Override
    protected void remove( Object resource )
    {
        FontResource fontResource = (FontResource) resource;

        this.editor.resources.removeFont(fontResource.getName());
        this.rebuildTable();

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
        Itchy.singleton.getGame().showWindow(this.openDialog);
    }

    public void onAdd( File file )
    {
        Itchy.singleton.getGame().hideWindow(this.openDialog);

        if (file != null) {
            String filename = this.editor.resources.makeRelativeFilename(file);
            String name = Util.nameFromFilename(filename);

            this.currentFontResource = new FontResource(this.editor.resources, name, filename);
            this.adding = true;
            this.showDetails(this.currentFontResource);
        }
    }

    protected void onRename()
    {
        if (!this.editor.resources.rename(this.currentFontResource.filename,
                this.txtFilename.getText())) {
            this.setMessage("Rename failed");
        } else {
            this.currentFontResource.filename = this.txtFilename.getText();
            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

    }

}
