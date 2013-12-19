/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.SceneResource;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;

public class ScenesEditor extends SubEditor<SceneResource>
{
    public ScenesEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Table createTable()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);

        TableModel model = this.createTableModel();
        Table table = new Table(model, columns);

        return table;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String sceneName : this.editor.resources.sceneNames()) {
            SceneResource sceneResource = this.editor.resources.getSceneResource(sceneName);
            String[] attributeNames = { "name" };
            TableModelRow row = new ReflectionTableModelRow<SceneResource>(sceneResource, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void addListButtons( Container buttons )
    {
        Button duplicate = new Button(new Label("Duplicate"));
        duplicate.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                ScenesEditor.this.onDuplicate();
            }
        });
        buttons.addChild(duplicate);

        super.addListButtons(buttons);
    }

    @Override
    protected void remove( SceneResource sceneResource )
    {
        this.editor.resources.removeScene(sceneResource.getName());
    }

    @Override
    protected void onAdd()
    {
        this.edit(new SceneResource(this.editor.resources, ""), true);
    }

    private void onDuplicate()
    {
        ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) this.table
            .getCurrentTableModelRow();
        if (row == null) {
            return;
        }
        SceneResource oldSceneResource = (SceneResource) row.getData();
        SceneResource newSceneResource = new SceneResource(this.editor.resources, "newScene");
        try {
            newSceneResource.setScene(oldSceneResource.loadScene().copy());
            oldSceneResource.unloadScene();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.edit(newSceneResource, true);
    }

    @Override
    public void onEdit()
    {
        ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) this.table.getCurrentTableModelRow();
        if (row == null) {
            return;
        }
        design((SceneResource) row.getData());
    }

    @Override
    protected void update() throws MessageException
    {
        TextBox name = (TextBox) this.form.getComponent("name");
        ClassNameBox sceneDirectorClassName = (ClassNameBox) this.form.getComponent("sceneDirectorClassName");

        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {
            if (getResources().getSceneResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }
        try {
            this.currentResource.rename(name.getText());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MessageException("Failed to rename the scene file.");
        }

        ClassName className = sceneDirectorClassName.getClassName();
        if (!getResources().checkClassName(className)) {
            throw new MessageException("Invalid Scene Role");
        }

        if (this.adding) {
            try {
                this.currentResource.save();
            } catch (Exception e) {
                e.printStackTrace();
                throw new MessageException("Failed to create empty scene file");
            }

            this.editor.resources.addScene(this.currentResource);
        }

        super.update();

        try {
            this.currentResource.save();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MessageException("Failed to save scene file");
        }
    }

    public void design( String sceneName )
    {
        design(this.editor.resources.getSceneResource(sceneName));

    }

    public void refresh()
    {
        this.rebuildTable();
    }

    public void design( SceneResource sceneResource )
    {
        SceneDesigner designer = new SceneDesigner(this.editor, sceneResource);
        this.editor.root.hide();
        designer.go();
    }

    @Override
    protected List<AbstractProperty<SceneResource, ?>> getProperties()
    {
        return SceneResource.properties;
    }
}
