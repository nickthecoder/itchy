/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileOpenDialog extends Window
{
    private Container ancestors;

    private File directory;

    private Table table;

    private SimpleTableModel tableModel;

    private Label message;

    public FileOpenDialog()
    {
        this("Open");
        this.clientArea.setLayout(new VerticalLayout());
        this.clientArea.setFill(true, true);

        this.ancestors = new Container();
        this.ancestors.addStyle("combo");
        this.clientArea.addChild(this.ancestors);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        TableModelColumn nameColumn = new TableModelColumn("Name", 1, 300);
        columns.add(nameColumn);
        this.tableModel = new SimpleTableModel();
        this.table = new Table(this.tableModel, columns);
        this.table.addTableListener(new TableListener() {
            @Override
            public void onRowSelected( TableRow tableRow )
            {
            }

            @Override
            public void onRowPicked( TableRow tableRow )
            {
                FileOpenDialog.this.onOk();
            }
        });

        this.clientArea.addChild(this.table);

        this.message = new Label(" ");
        this.message.setVisible(false);
        this.message.addStyle("error");
        this.clientArea.addChild(this.message);

        Container buttonBar = new Container();
        buttonBar.addStyle("buttonBar");
        buttonBar.setXAlignment(0.5f);
        this.clientArea.addChild(buttonBar);

        Button ok = new Button("Ok");
        ok.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FileOpenDialog.this.onOk();
            }
        });
        buttonBar.addChild(ok);

        Button cancel = new Button("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FileOpenDialog.this.onCancel();
            }
        });
        buttonBar.addChild(cancel);

    }

    public FileOpenDialog( String title )
    {
        super(title);
    }


    public void setDirectory( File directory )
    {
        setAncestorDirectory( directory );
        
        this.ancestors.clear();
        this.addAncestor(directory, true);
    }
    
    public void setAncestorDirectory( File directory )
    {
        this.directory = directory;
        this.tableModel.clear();

        File[] children = this.directory.listFiles();
        Arrays.sort(children);

        for (File child : children) {
            if (child.isDirectory()) {
                this.addFile(child);
            }
        }
        for (File child : children) {
            if (child.isFile()) {
                this.addFile(child);
            }
        }

        this.table.reset();

    }

    private void addAncestor( final File directory, boolean selected )
    {
        if (directory.getParentFile() != null) {
            this.addAncestor(directory.getParentFile(), false);
        }
        final Button button = new Button(directory.getName());
        button.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                FileOpenDialog.this.setAncestorDirectory(directory);
                for ( Component other : button.getParent().getChildren()) {
                   other.removeStyle("selected");
                }
                button.addStyle("selected");
            }
        });
        if ( selected ) {
            button.addStyle("selected");
        }
        
        this.ancestors.addChild(button);
    }

    private void addFile( File child )
    {
        if (!child.isHidden()) {
            SimpleTableModelRow row = new SimpleTableModelRow();
            row.add(child);
            row.add(child.getName());
            this.tableModel.addRow(row);
        }
    }

    public void setMessage( String message )
    {
        if (this.message == null) {
            this.message.setText(" ");
            this.message.setVisible(false);
        } else {
            this.message.setText(message);
            this.message.setVisible(true);
        }
        this.forceLayout();
    }

    private void onOk()
    {
        TableModelRow row = this.table.getCurrentTableModelRow();
        if (row == null) {
            return;
        }

        File file = (File) row.getData(0);
        if (file.isDirectory()) {
            this.setDirectory(file);
        } else {
            this.onChosen(file);
        }
    }

    private void onCancel()
    {
        this.onChosen(null);
    }

    /**
     * Called when the user has finished with the dialog, either by Ok or Cancel.
     * 
     * @param file
     *        The file that was chosen or null if no file were chosen.
     */
    protected void onChosen( File file )
    {

    }
}
