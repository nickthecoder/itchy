/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.Picker;
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
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class CostumesEditor extends SubEditor
{
    private static final int EVENT_RESOURCE_COLUMN = 3;

    private TextBox txtName;

    private CostumeResource currentCostumeResource;

    private Label labelExtendedFrom;

    private Button buttonExtendedFrom;

    private ComboBox behaviour;

    private Table eventsTable;

    private SimpleTableModel eventsTableModel;

    public CostumesEditor( Editor editor )
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

        TableModelColumn extendedFrom = new TableModelColumn("Extends", 2, 200);
        extendedFrom.rowComparator = new SingleColumnRowComparator<String>(2);

        TableModelColumn image = new TableModelColumn("\"default\" Pose", 1, 150) {
            public Component createLabelOrImage( TableModelRow row )
            {
                Costume costume = (Costume) row.getData(this.index);

                Surface surface = CostumesEditor.this.editor.resources.getThumbnail(costume);
                if (surface == null) {
                    return new Label("none");
                } else {
                    return new ImageComponent(surface);
                }
            }

            @Override
            public Component createCell( TableModelRow row )
            {
                Container container = new Container();
                container.addChild(this.createLabelOrImage(row));
                return container;
            }

            @Override
            public void updateComponent( Component component, TableModelRow row )
            {
                Container container = (Container) component;
                container.clear();
                container.addChild(this.createLabelOrImage(row));
            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(extendedFrom);
        columns.add(image);

        TableModel model = this.createTableModel();
        this.table = new Table(model, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                CostumesEditor.this.onEdit();
            }

        });
        this.table.sort(0);
        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        form.addChild(this.table);

        form.addChild(this.createListButtons());
        this.table.sort(0);

        return form;
    }

    private TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        String[] attributeNames = { "name", "costume", "extendedFromName" };
        for (String costumeName : this.editor.resources.costumeNames()) {
            CostumeResource costumeResource = this.editor.resources.getCostumeResource(costumeName);
            TableModelRow row = new ReflectionTableModelRow<CostumeResource>(costumeResource,
                attributeNames);
            model.addRow(row);
        }
        return model;
    }

    private void rebuildTable()
    {
        TableModel model = this.createTableModel();
        this.table.setTableModel(model);
    }

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentCostumeResource = (CostumeResource) resource;

        this.txtName = new TextBox(this.currentCostumeResource.getName());

        Costume base = this.currentCostumeResource.costume.getExtendedFrom();
        this.labelExtendedFrom = new Label(base == null ? "None" : this.editor.resources
            .getCostumeResource(base).getName());
        this.buttonExtendedFrom = new Button(this.labelExtendedFrom) {
            @Override
            public void onClick( MouseButtonEvent e )
            {
                CostumePicker picker = new CostumePicker(CostumesEditor.this.editor.resources,
                    "None") {
                    @Override
                    public void pick( CostumeResource costumeResource )
                    {
                        CostumesEditor.this.labelExtendedFrom
                            .setText(costumeResource == null ? "None" : costumeResource
                                .getName());
                    }
                };
                picker.show();
            };
        };

        this.behaviour = new ComboBox(
            this.currentCostumeResource.costume.behaviourClassName,
            this.editor.game.resources.getBehaviourClassNames());

        grid.addRow("Name", this.txtName);
        grid.addRow("Extends", this.buttonExtendedFrom);
        grid.addRow("Behaviour", this.behaviour);

        this.eventsTable = this.createEventsTable();
        this.eventsTable.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                CostumesEditor.this.onEditEvent();
            }
        });

        this.eventsTable.sort(0);
        grid.addRow("Events", this.eventsTable);
    }

    private Table createEventsTable()
    {
        TableModelColumn eventColumn = new TableModelColumn("Event", 0, 200);
        eventColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn typeColumn = new TableModelColumn("Type", 1, 100);
        typeColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn nameColumn = new TableModelColumn("Resource", 2, 200);
        nameColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn previewColumn = new TableModelColumn("", 3, 140) {
            public void addPlainCell( Container container, final TableModelRow row )
            {
                final Object data = row.getData(3);
                if (data instanceof String) {
                    container.addChild(new Label((String) data));
                } else if (data instanceof PoseResource) {
                    PoseResource resource = (PoseResource) data;
                    container.addChild(new ImageComponent(resource.getThumbnail()));
                } else if (data instanceof ManagedSound) {
                    Button button = new Button("Play");
                    button.addActionListener(new ActionListener() {
                        @Override
                        public void action()
                        {
                            ManagedSound cs = (ManagedSound) data;
                            SoundResource resource = cs.soundResource;
                            resource.getSound().play();
                        }
                    });
                    container.addChild(button);
                }
            }

            @Override
            public Component createCell( TableModelRow row )
            {
                Container container = new Container();
                this.addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent( Component component, TableModelRow row )
            {
                Container container = (Container) component;
                container.clear();
                this.addPlainCell(container, row);
            };
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(eventColumn);
        columns.add(typeColumn);
        columns.add(nameColumn);
        columns.add(previewColumn);

        this.eventsTableModel = this.createEventsTableModel();
        return new Table(this.eventsTableModel, columns);

    }

    private SimpleTableModel createEventsTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        Costume costume = this.currentCostumeResource.costume;

        for (String name : costume.getPoseNames()) {
            for (PoseResource poseResource : costume.getPoseChoices(name)) {

                SimpleTableModelRow row = new SimpleTableModelRow();
                row.add(name);
                row.add("Pose");
                row.add(poseResource.getName());
                row.add(poseResource);

                model.addRow(row);
            }
        }
        for (String name : costume.getAnimationNames()) {
            for (AnimationResource animationResource : costume.getAnimationChoices(name)) {

                SimpleTableModelRow row = new SimpleTableModelRow();
                row.add(name);
                row.add("Animation");
                row.add(animationResource.getName());
                row.add(animationResource);

                model.addRow(row);
            }
        }
        for (String name : costume.getSoundNames()) {
            for (ManagedSound costumeSound : costume.getSoundChoices(name)) {

                SimpleTableModelRow row = new SimpleTableModelRow();
                row.add(name);
                row.add("Sound");
                row.add(costumeSound.soundResource.getName());
                row.add(costumeSound);

                model.addRow(row);
            }
        }
        for (String name : costume.getFontNames()) {
            for (FontResource fontResource : costume.getFontChoices(name)) {

                SimpleTableModelRow row = new SimpleTableModelRow();
                row.add(name);
                row.add("Font");
                row.add(fontResource.getName());
                row.add(fontResource);

                model.addRow(row);
            }
        }
        for (String name : costume.getStringNames()) {
            for (String string : costume.getStringChoices(name)) {

                SimpleTableModelRow row = new SimpleTableModelRow();
                row.add(name);
                row.add("Font");
                row.add(string);
                row.add(string);

                model.addRow(row);
            }
        }

        return model;
    }

    @Override
    protected void addDetailButtons( Container buttons )
    {
        Button edit = new Button("Edit");
        edit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumesEditor.this.onEditEvent();
            }
        });
        buttons.addChild(edit);

        Button add = new Button("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumesEditor.this.onAddEvent();
            }
        });
        buttons.addChild(add);

        Button remove = new Button("Remove");
        remove.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumesEditor.this.onRemoveEvent();
            }
        });
        buttons.addChild(remove);

        super.addDetailButtons(buttons);
    }

    private void onAddEvent()
    {
        HashMap<String, Class<?>> pickList = new HashMap<String, Class<?>>();

        pickList.put("String", String.class);
        pickList.put("Pose", PoseResource.class);
        pickList.put("Animation", AnimationResource.class);
        pickList.put("Sound", ManagedSound.class);
        pickList.put("Font", FontResource.class);

        Picker<Class<?>> picker = new Picker<Class<?>>("Which Type?", pickList) {
            @Override
            public void pick( String string, Class<?> picked )
            {
                if (picked == String.class) {
                    CostumesEditor.this.onAddString();

                } else if (picked == PoseResource.class) {
                    CostumesEditor.this.onAddPose();

                } else if (picked == AnimationResource.class) {
                    CostumesEditor.this.onAddAnimation();

                } else if (picked == ManagedSound.class) {
                    CostumesEditor.this.onAddSound();

                } else if (picked == FontResource.class) {
                    CostumesEditor.this.onAddFont();
                }

            }
        };
        picker.show();
    }

    private void onAddString()
    {
    }

    private void onAddPose()
    {
        PosePicker picker = new PosePicker(this.editor.resources) {
            @Override
            public void pick( PoseResource poseResource )
            {
                Costume costume = CostumesEditor.this.currentCostumeResource.costume;
                costume.addPose("newEvent", poseResource);
                CostumesEditor.this.rebuiltEventTable();
            }
        };
        picker.show();
    }

    private void onAddAnimation()
    {
    }

    private void onAddSound()
    {
    }

    private void onAddFont()
    {
        FontPicker picker = new FontPicker(this.editor.resources) {
            @Override
            public void pick( FontResource fontResource )
            {
                Costume costume = CostumesEditor.this.currentCostumeResource.costume;
                costume.addFont("newEvent", fontResource);
                CostumesEditor.this.rebuiltEventTable();
            }
        };
        picker.show();
    }

    private void onRemoveEvent()
    {
        Costume costume = this.currentCostumeResource.costume;

        TableModelRow row = this.eventsTable.getCurrentTableModelRow();

        if (row != null) {

            Object data = row.getData(EVENT_RESOURCE_COLUMN);
            String name = (String) row.getData(0);

            if (data instanceof PoseResource) {
                costume.removePose(name, (PoseResource) data);

            } else if (data instanceof String) {
                costume.removeString(name, (String) data);

            } else if (data instanceof AnimationResource) {
                costume.removeAnimation(name, (AnimationResource) data);

            } else if (data instanceof FontResource) {
                costume.removeFont(name, (FontResource) data);

            } else if (data instanceof ManagedSound) {
                costume.removeSound(name, (ManagedSound) data);

            } else {
                System.err.println("Unknown data : " + data.getClass().getName());
            }

            this.rebuiltEventTable();
        }
    }

    private TextBox txtEventName;

    private TextBox txtEventString;

    private PosePickerButton eventPosePickerButton;

    private FontPickerButton eventFontPickerButton;
    
    private void onEditEvent()
    {
        TableModelRow row = this.eventsTable.getCurrentTableModelRow();
        String name = (String) row.getData(0);
        Object data = row.getData(EVENT_RESOURCE_COLUMN);

        if (row != null) {

            final Window window = new Window("Edit Event");
            window.clientArea.setFill(true, true);
            window.clientArea.setLayout(new VerticalLayout());

            Container form = new Container();
            form.addStyle("form");
            GridLayout grid = new GridLayout(form, 2);
            form.setLayout(grid);
            window.clientArea.addChild(form);

            this.txtEventName = new TextBox(name);
            grid.addRow("Event Name", this.txtEventName);

            if (data instanceof String) {
                this.txtEventString = new TextBox((String) data);
                grid.addRow("String", this.txtEventString);

            } else if (data instanceof PoseResource) {
                this.eventPosePickerButton = new PosePickerButton(this.getResources(),
                    (PoseResource) data);
                grid.addRow("Pose", this.eventPosePickerButton);

            } else if (data instanceof FontResource) {
                this.eventFontPickerButton = new FontPickerButton(this.getResources(),
                    (FontResource) data);
                grid.addRow("Pose", this.eventFontPickerButton);

            } else {
                System.err.println("Unexpected type in onEditEvent : " + data.getClass().getName());
            }

            Container buttons = new Container();
            buttons.addStyle("buttonBar");
            buttons.setXAlignment(0.5f);

            Button ok = new Button(new Label("Ok"));
            ok.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    CostumesEditor.this.onEditEventOk();
                    window.destroy();
                }
            });
            Button cancel = new Button(new Label("Cancel"));
            cancel.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    window.destroy();
                }
            });
            buttons.addChild(ok);
            buttons.addChild(cancel);

            window.clientArea.addChild(buttons);

            Itchy.getGame().showWindow(window);
        }
    }

    private void onEditEventOk()
    {
        Costume costume = this.currentCostumeResource.costume;
        TableModelRow row = this.eventsTable.getCurrentTableModelRow();

        if (row != null) {

            String name = (String) row.getData(0);
            Object data = row.getData(EVENT_RESOURCE_COLUMN);
            onRemoveEvent();

            if (data instanceof String) {
                costume.addString(name, (String) data);

            } else if (data instanceof PoseResource) {
                costume.addPose(name, this.eventPosePickerButton.getValue());

            } else if (data instanceof FontResource) {
                costume.addFont(name, this.eventFontPickerButton.getValue());

            } else {
                System.err.println("Unexpected type in onEditEventOk : " +
                    data.getClass().getName());
            }
        }

        CostumesEditor.this.rebuiltEventTable();

    }

    private void rebuiltEventTable()
    {
        this.eventsTable.setTableModel(this.createEventsTableModel());
    }

    @Override
    protected void onOk()
    {
        if (this.adding || (!this.txtName.getText().equals(this.currentCostumeResource.getName()))) {
            if (this.editor.resources.getCostumeResource(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }

        if (!Behaviour.isValidClassName(this.behaviour.getText())) {
            this.setMessage("Not a valid behaviour class name");
            return;
        }

        Costume oldBase = this.currentCostumeResource.costume.getExtendedFrom();
        if (this.labelExtendedFrom.getText().equals("None")) {
            this.currentCostumeResource.costume.setExtendedFrom(null);
        } else {
            Costume base = this.editor.resources.getCostume(this.labelExtendedFrom.getText());
            this.currentCostumeResource.costume.setExtendedFrom(base);
            Costume costume = base;
            for (int i = 0; i < 100; i++) {
                if (costume == null) {
                    break;
                }
                costume = costume.getExtendedFrom();
                if (i == 99) {
                    this.currentCostumeResource.costume.setExtendedFrom(oldBase);
                    this.setMessage("Bad Extends - forms a loop.");
                    return;
                }
            }
        }

        this.currentCostumeResource.setName(this.txtName.getText());

        this.currentCostumeResource.costume.behaviourClassName = this.behaviour.getText();

        if (this.adding) {
            this.editor.resources.addCostume(this.currentCostumeResource);
            this.rebuildTable();
        } else {
            this.table.updateRow(this.table.getCurrentTableModelRow());
        }
        Itchy.getGame().hideWindow(this.editWindow);

    }

    @Override
    protected void remove( Object resource )
    {
        CostumeResource costumeResource = (CostumeResource) resource;

        this.editor.resources.removeCostume(costumeResource.getName());
        this.rebuildTable();

    }

    @Override
    protected void onAdd()
    {
        PosePicker posePicker = new PosePicker(this.editor.resources) {
            @Override
            public void pick( PoseResource poseResource )
            {
                CostumesEditor.this.add(poseResource);
            }
        };
        posePicker.show();
    }

    private void add( PoseResource poseResouerce )
    {
        Costume costume = new Costume();
        costume.addPose("default", poseResouerce);
        this.currentCostumeResource = new CostumeResource(this.editor.resources,
            poseResouerce.getName(), costume);
        this.adding = true;
        this.showDetails(this.currentCostumeResource);

    }
}
