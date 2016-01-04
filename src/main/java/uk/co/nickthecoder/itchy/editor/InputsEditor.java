/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.InputResource;
import uk.co.nickthecoder.itchy.KeyInput;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.property.AbstractProperty;

public class InputsEditor extends SubEditor<InputResource>
{
    public InputsEditor( Editor editor )
    {
        super(editor);
    }

    private TextBox keysText;

    @Override
    public void addHeader( Container page )
    {
    }

    @Override
    public Table createTable()
    {

        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn keys = new TableModelColumn("Keys", 1, 300);
        keys.rowComparator = new SingleColumnRowComparator<String>(1);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(keys);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    protected SimpleTableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String inputName : this.editor.resources.inputNames()) {
            InputResource inputResource = this.editor.resources.getInputResource(inputName);
            String[] attributeNames = { "name", "input.keys" };
            TableModelRow row = new ReflectionTableModelRow<InputResource>(inputResource, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected Component createForm()
    {
        super.createForm();

        GridLayout grid = this.form.grid;

        PlainContainer container = new PlainContainer();
        container.addStyle("combo");
        this.keysText = new TextBox( this.currentResource.getInput().getKeys() );
        Button keysButton = new Button("+");
        keysButton.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onAddKey();
            }

        });

        container.addChild(this.keysText);
        container.addChild(keysButton);

        grid.addRow("Keys", container);
        
        return this.form.container;
    }

    @Override
    protected void update() throws MessageException
    {
        TextBox name = (TextBox) this.form.getComponent("name");

        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {
            if (this.editor.resources.getInputResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        try {
            this.currentResource.getInput().parseKeys(this.keysText.getText());
        } catch (Exception e) {
            throw new MessageException(e.getMessage());
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addInput(this.currentResource);
        }
    }

    @Override
    protected void remove( InputResource inputResource )
    {
        this.editor.resources.removeInput(inputResource.getName());
    }

    @Override
    protected void onAdd()
    {
        this.edit(new InputResource(this.editor.resources, "new", new Input()), true);
    }

    @Override
    protected List<AbstractProperty<InputResource, ?>> getProperties()
    {
        return InputResource.properties;
    }

    protected void onAddKey()
    {
        KeyInputPicker keyPicker = new KeyInputPicker() {
            @Override
            public void pick(KeyInput keyInput)
            {
                String old = InputsEditor.this.keysText.getText().trim();
                if (old.length() > 0) {
                    old = old + ",";
                }
                InputsEditor.this.keysText.setText(old + keyInput.toString());
            }
        };
        keyPicker.show();
    }
}
