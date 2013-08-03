/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.NullSceneBehaviour;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
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

public class ScenesEditor extends SubEditor
{
    private TextBox txtName;

    private CheckBox checkBoxShowMouse;

    private SceneResource currentSceneResource;

    public ScenesEditor( Editor editor )
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

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);

        TableModel model = this.createTableModel();
        this.table = new Table(model, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                ScenesEditor.this.onDesign();
            }
        });

        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        this.table.sort(0);

        form.addChild(this.table);
        form.addChild(this.createListButtons());

        return form;
    }

    @Override
    protected void addListButtons( Container buttons )
    {
        Button design = new Button(new Label("Design"));
        design.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                ScenesEditor.this.onDesign();
            }
        });
        buttons.addChild(design);

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

    private TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String sceneName : this.editor.resources.sceneNames()) {
            SceneResource sceneResource = this.editor.resources.getSceneResource(sceneName);
            String[] attributeNames = { "name" };
            TableModelRow row = new ReflectionTableModelRow<SceneResource>(sceneResource,
                attributeNames);
            model.addRow(row);
        }
        return model;
    }

    private void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    private ComboBox sceneBehaviourName;

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentSceneResource = (SceneResource) resource;

        this.txtName = new TextBox(this.currentSceneResource.getName());
        grid.addRow(new Label("Name"), this.txtName);

        String behaviourName = NullSceneBehaviour.class.getName();

        boolean showMouse = false;
        try {
            Scene scene = this.currentSceneResource.getScene();
            showMouse = scene.showMouse;
            behaviourName = scene.sceneBehaviourName;

        } catch (Exception e) {
            // Do nothing
        }

        this.sceneBehaviourName = new ComboBox(behaviourName,
            this.editor.game.resources.getSceneBehaviourClassNames());
        this.sceneBehaviourName.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                ComboBox box = ScenesEditor.this.sceneBehaviourName;
                String value = box.getText();
                boolean ok = ScenesEditor.this.editor.game.resources
                    .registerSceneBehaviourClassName(value);
                box.addStyle("error", !ok);
            }
        });

        grid.addRow("Scene Behaviour", this.sceneBehaviourName);

        this.checkBoxShowMouse = new CheckBox(showMouse);
        grid.addRow("Show Mouse", this.checkBoxShowMouse);

    }

    @Override
    protected void onOk()
    {
        if (this.adding || (!this.txtName.getText().equals(this.currentSceneResource.getName()))) {
            if (getResources().getSceneResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }

        if (!getResources().registerSceneBehaviourClassName(this.sceneBehaviourName.getText())) {
            this.setMessage("Invalid Scene Behaviour");
            return;
        }
        this.currentSceneResource.rename(this.txtName.getText());

        if (this.adding) {
            try {
                this.currentSceneResource.save();
            } catch (Exception e) {
                e.printStackTrace();
                this.setMessage("Failed to create empty scene file");
                return;
            }

            this.editor.resources.addScene(this.currentSceneResource);
            this.rebuildTable();

        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

        try {
            this.currentSceneResource.getScene().showMouse = this.checkBoxShowMouse.getValue();
            this.currentSceneResource.getScene().sceneBehaviourName = this.sceneBehaviourName.getText();
            this.currentSceneResource.save();
        } catch (Exception e) {
            e.printStackTrace();
            this.setMessage("Failed to save scene file");
            return;
        }

        Itchy.singleton.getGame().hideWindow(this.editWindow);

    }

    @Override
    protected void remove( Object resource )
    {
        SceneResource sceneResource = (SceneResource) resource;

        this.editor.resources.removeScene(sceneResource.getName());
        this.rebuildTable();

    }

    @Override
    protected void onAdd()
    {
        this.currentSceneResource = new SceneResource(this.editor.resources, "newScene");
        this.adding = true;
        this.showDetails(this.currentSceneResource);
    }

    private void onDuplicate()
    {
        ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) this.table
            .getCurrentTableModelRow();
        if (row == null) {
            return;
        }
        SceneResource sceneResource = (SceneResource) row.getData();

        this.currentSceneResource = new SceneResource(this.editor.resources, "newScene");
        try {
            this.currentSceneResource.setScene(sceneResource.getScene().copy());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.adding = true;
        this.showDetails(this.currentSceneResource);
    }

    private void onDesign()
    {
        ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) this.table
            .getCurrentTableModelRow();
        if (row == null) {
            return;
        }
        SceneResource sceneResource = (SceneResource) row.getData();

        SceneDesigner designer = new SceneDesigner(this.editor, sceneResource);
        this.editor.mainGuiPose.hide();
        designer.go();
    }
}
