package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.SceneResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.CheckBox;
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

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentSceneResource = (SceneResource) resource;

        this.txtName = new TextBox(this.currentSceneResource.getName());
        grid.addRow(new Label("Name"), this.txtName);
        
        try {
            this.checkBoxShowMouse = new CheckBox(this.currentSceneResource.getScene().showMouse);
            grid.addRow(new Label("Show Mouse"), this.checkBoxShowMouse);
        } catch (Exception e) {
            // Do nothing
        }
        
    }

    @Override
    protected void onOk()
    {
        if (this.adding || (!this.txtName.getText().equals(this.currentSceneResource.getName()))) {
            if (this.editor.resources.getSceneResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }

        File directory = new File(this.editor.resources.resolveFilename("scenes"));
        directory.mkdirs();

        File file = new File("scenes", this.txtName.getText() + ".xml");
        String filename = file.getPath();

        if (this.adding) {
            try {
                this.currentSceneResource.setFilename(filename);
                this.currentSceneResource.save();
            } catch (Exception e) {
                e.printStackTrace();
                this.setMessage("Failed to create empty scene file");
                return;
            }

        } else {
            if (!this.currentSceneResource.getFilename().equals(filename)) {
                if (!this.editor.resources
                        .rename(this.currentSceneResource.getFilename(), filename)) {
                    this.setMessage("Rename failed");
                    return;
                }
            }
        }

        this.currentSceneResource.rename(this.txtName.getText());
        this.currentSceneResource.setFilename(filename);


        if (this.adding) {
            this.editor.resources.addScene(this.currentSceneResource);
            this.rebuildTable();
        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

        try {
            this.currentSceneResource.getScene().showMouse = this.checkBoxShowMouse.getValue();
            this.currentSceneResource.save();
        } catch (Exception e) {
            e.printStackTrace();
            this.setMessage("Failed to save scene file");            
        }
        Itchy.singleton.hideWindow(this.editWindow);

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
        this.currentSceneResource = new SceneResource(this.editor.resources, "newScene", "");
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

        this.currentSceneResource = new SceneResource(this.editor.resources, "newScene", "");
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
