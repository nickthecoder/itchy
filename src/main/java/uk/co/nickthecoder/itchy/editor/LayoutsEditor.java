/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Stage;
import uk.co.nickthecoder.itchy.StageConstraint;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PropertiesForm;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SimpleTableModelRow;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.Window;
import uk.co.nickthecoder.itchy.property.Property;

public class LayoutsEditor extends SubEditor<Layout>
{
    public LayoutsEditor(Editor editor)
    {
        super(editor);
    }

    private Table layersTable;

    private TableModel layersTableModel;

    @Override
    public void addHeader(Container page)
    {
    }

    @Override
    public Table createTable()
    {

        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    protected SimpleTableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String name : this.editor.resources.layoutNames()) {
            Layout layout = this.editor.resources.getLayout(name);
            String[] attributeNames = { "name" };
            TableModelRow row = new ReflectionTableModelRow<Layout>(layout, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected Component createForm()
    {
        super.createForm();

        PlainContainer layersTableSection = new PlainContainer();

        this.layersTable = this.createLayersTable();
        this.layersTable.addTableListener(new AbstractTableListener()
        {
            @Override
            public void onRowPicked(TableRow tableRow)
            {
                onEditLayer();
            }
        });

        this.layersTable.sort(0);
        layersTableSection.addChild(this.layersTable);

        PlainContainer tableButtons = new PlainContainer();
        tableButtons.setLayout(new VerticalLayout());
        tableButtons.setYAlignment(0.5f);
        layersTableSection.setFill(true, true);
        tableButtons.addStyle("buttonColumn");

        layersTableSection.addChild(tableButtons);

        GuiButton edit = new GuiButton("Edit");
        edit.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onEditLayer();
            }
        });
        tableButtons.addChild(edit);

        GuiButton add = new GuiButton("Add");
        add.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onAddLayer();
            }
        });
        tableButtons.addChild(add);

        GuiButton remove = new GuiButton("Remove");
        remove.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onRemoveLayer();
            }
        });
        tableButtons.addChild(remove);

        this.form.grid.addRow(new Label("Layers"), layersTableSection);
        return this.form.container;
    }

    @Override
    protected void update() throws MessageException
    {
        TextBox nameBox = (TextBox) this.form.getComponent("name");
        String oldName = (String) this.form.getRevertValue("name");
        String newName = nameBox.getText();

        if (this.adding || (!newName.equals(oldName))) {
            if (this.editor.resources.getLayout(nameBox.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();

        if ((!this.adding) && (!newName.equals(oldName))) {
            getResources().removeLayout(oldName);
            getResources().addLayout(this.currentResource);
        }

        if (this.adding) {
            getResources().addLayout(this.currentResource);
        }
    }

    @Override
    protected void remove(Layout layout)
    {
        getResources().removeLayout(layout.getName());
    }

    @Override
    protected void onAdd()
    {
        this.edit(new Layout(), true);
    }

    private Table createLayersTable()
    {
        TableModelColumn nameColumn = new TableModelColumn("Layer Name", 0, 200);
        nameColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn zOrderColumn = new TableModelColumn("Z Order", 1, 100);
        zOrderColumn.rowComparator = new SingleColumnRowComparator<Integer>(1);

        TableModelColumn xColumn = new TableModelColumn("X", 2, 50);
        xColumn.rowComparator = new SingleColumnRowComparator<Integer>(2);

        TableModelColumn yColumn = new TableModelColumn("Y", 3, 70);
        yColumn.rowComparator = new SingleColumnRowComparator<Integer>(3);

        TableModelColumn widthColumn = new TableModelColumn("Width", 4, 70);
        widthColumn.rowComparator = new SingleColumnRowComparator<Integer>(4);

        TableModelColumn heightColumn = new TableModelColumn("Height", 5, 70);
        heightColumn.rowComparator = new SingleColumnRowComparator<Integer>(5);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(nameColumn);
        columns.add(zOrderColumn);
        columns.add(xColumn);
        columns.add(yColumn);
        columns.add(widthColumn);
        columns.add(heightColumn);

        this.layersTableModel = this.createLayersTableModel();
        Table table = new Table(this.layersTableModel, columns);
        table.sort(2);
        
        return table;

    }

    private static final int DATA_COLUMN = 6;

    private SimpleTableModel createLayersTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (Layer layer : this.currentResource.layers) {
            SimpleTableModelRow row = new SimpleTableModelRow();
            row.add(layer.name);
            row.add(layer.zOrder);
            row.add(layer.position.x);
            row.add(layer.position.y);
            row.add(layer.position.width);
            row.add(layer.position.height);
            row.add(layer);

            model.addRow(row);
        }

        return model;
    }

    private void rebuildLayersTable()
    {
        this.layersTableModel = this.createLayersTableModel();
        this.layersTable.setTableModel(this.layersTableModel);
    }

    private void selectLayersTableRow(Layer layer)
    {
        for (int i = 0; i < this.layersTableModel.getRowCount(); i++) {
            TableModelRow row = this.layersTableModel.getRow(i);
            if (row.getData(DATA_COLUMN) == layer) {
                this.layersTable.selectRow(row);
                return;
            }
        }
    }

    protected void onAddLayer()
    {
        Layer layer = new Layer();
        layer.position.width = getResources().game.getWidth();
        layer.position.height = getResources().game.getHeight();
        editLayer(layer);
    }

    protected void onEditLayer()
    {
        TableModelRow row = this.layersTable.getCurrentTableModelRow();
        if (row == null) {
            return;
        }

        Layer layer = (Layer) row.getData(DATA_COLUMN);

        if (row != null) {
            editLayer(layer);
        }
    }

    protected void onRemoveLayer()
    {
        TableModelRow row = this.layersTable.getCurrentTableModelRow();
        if (row == null) {
            return;
        }

        Layer layer = (Layer) row.getData(DATA_COLUMN);

        currentResource.removeLayer(layer);
        rebuildLayersTable();
    }

    private Layer editingLayer;

    private PropertiesForm<Layer> layerForm;

    private Notebook editNotebook;

    private void editLayer(final Layer layer)
    {
        this.editingLayer = layer;

        final Window window = new Window("Edit Layer");
        window.clientArea.setFill(true, true);
        window.clientArea.setLayout(new VerticalLayout());

        editNotebook = new Notebook();
        window.clientArea.addChild(editNotebook);

        this.layerForm = new PropertiesForm<Layer>(layer, layer.getProperties());
        this.layerForm.autoUpdate = true;

        stagePropertiesContainer = new PlainContainer();
        viewPropertiesContainer = new PlainContainer();
        stageConstraintPropertiesContainer = new PlainContainer();
        
        editNotebook.addPage("Details", this.layerForm.createForm());
        editNotebook.addPage("View Properties", viewPropertiesContainer);
        editNotebook.addPage("Stage Properties", stagePropertiesContainer);
        editNotebook.addPage("Stage Constraint", stageConstraintPropertiesContainer);

        createViewProperties();
        createStageProperties();
        createStageConstraintProperties();

        final ClassNameBox viewClassNameBox = (ClassNameBox) this.layerForm.getComponent("viewClassName");
        viewClassNameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (viewClassNameBox.isValid()) {
                    createViewProperties();
                    createStageProperties();
                    createStageConstraintProperties();
                }
            }
        });

        final ClassNameBox stageClassNameBox = (ClassNameBox) this.layerForm.getComponent("stageClassName");
        stageClassNameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (stageClassNameBox.isValid()) {
                    createStageProperties();
                    createStageConstraintProperties();
                }
            }
        });
        
        final ClassNameBox stageConstraintClassNameBox = (ClassNameBox) this.layerForm.getComponent("stageConstraintClassName");
        stageConstraintClassNameBox.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                if (stageConstraintClassNameBox.isValid()) {
                    createStageConstraintProperties();
                }
            }
        });

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(0.5f);

        GuiButton ok = new GuiButton(new Label("Ok"));
        ok.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                try {
                    updateLayer();
                } catch (MessageException e) {
                    // TODO Show the message.
                    return;
                }
                onEditLayerOk();
                window.hide();
            }
        });
        GuiButton cancel = new GuiButton(new Label("Cancel"));
        cancel.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                layerForm.revert();
                window.hide();
            }
        });
        buttons.addChild(ok);
        buttons.addChild(cancel);

        window.clientArea.addChild(buttons);

        window.show();
    }

    private PropertiesForm<View> viewPropertiesForm;

    private Container viewPropertiesContainer;

    private void createViewProperties()
    {
        viewPropertiesContainer.clear();
        
        View view = this.editingLayer.getView();
        viewPropertiesForm = new PropertiesForm<View>(view, view.getProperties());

        viewPropertiesContainer.addChild(viewPropertiesForm.createForm());
    }

    private PropertiesForm<Stage> stagePropertiesForm;

    private Container stagePropertiesContainer;

    private void createStageProperties()
    {
        stagePropertiesForm = null;
        
        stagePropertiesContainer.clear();

        StageView stageView = this.editingLayer.getStageView();
        boolean hasStage = stageView != null;

        if (hasStage) {

            Stage stage = stageView.getStage();
            if (stage != null) {

                stagePropertiesForm = new PropertiesForm<Stage>(stage, stage.getProperties());
                stagePropertiesContainer.addChild(stagePropertiesForm.createForm());

            }
        }

        this.layerForm.getComponent("stageClassName").setVisible(hasStage);
        this.layerForm.getComponent("stageConstraintClassName").setVisible(hasStage);
        this.editNotebook.getTab(2).setVisible(hasStage);
        this.editNotebook.getTab(3).setVisible(hasStage);

    }


    private PropertiesForm<StageConstraint> stageConstraintPropertiesForm;

    private Container stageConstraintPropertiesContainer;

    private void createStageConstraintProperties()
    {
        stageConstraintPropertiesForm = null;
        
        stageConstraintPropertiesContainer.clear();

        StageView stageView = this.editingLayer.getStageView();

        if (stageView != null) {

            Stage stage = stageView.getStage();
            if (stage != null) {
                StageConstraint stageConstraint = stage.getStageConstraint();
                
                stageConstraintPropertiesForm = new PropertiesForm<StageConstraint>(stageConstraint, stageConstraint.getProperties());
                stageConstraintPropertiesContainer.addChild(stageConstraintPropertiesForm.createForm());

            }
        }

    }

    private void updateLayer() throws MessageException
    {
    }

    private void onEditLayerOk()
    {
        // layerForm is autoUpdate, so no need to update it.
        // layerForm.update();
        viewPropertiesForm.update();
        if (stagePropertiesForm != null) {
            stagePropertiesForm.update();
        }
        if (stageConstraintPropertiesForm != null) {
            stageConstraintPropertiesForm.update();
        }
        
        if (this.currentResource.layers.contains(this.editingLayer)) {
        } else {
            // New
            this.currentResource.addLayer(this.editingLayer);
        }
        this.rebuildLayersTable();
        this.selectLayersTableRow(this.editingLayer);
        this.editingLayer = null;
    }

    @Override
    protected List<Property<Layout, ?>> getProperties()
    {
        return this.currentResource.getProperties();
    }

}
