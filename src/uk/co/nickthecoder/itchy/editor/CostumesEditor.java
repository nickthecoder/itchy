/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeProperties;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.Picker;
import uk.co.nickthecoder.itchy.gui.PickerButton;
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
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class CostumesEditor extends SubEditor<CostumeResource>
{
    private static final int EVENT_RESOURCE_COLUMN = 3;

    private static final String NEW_EVENT_NAME = "default";

    private Notebook notebook;

    private ClassNameBox propertiesClassName;

    private Table eventsTable;

    private SimpleTableModel eventsTableModel;

    private Container propertiesContainer;

    private Label labelExtendedFrom;

    private Button buttonExtendedFrom;

    public CostumesEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Table createTable()
    {
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
        Table table = new Table(model, columns);

        return table;
    }

    @Override
    protected TableModel createTableModel()
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

    @Override
    protected void createForm()
    {

        super.createForm();

        final Costume costume = this.currentResource.getCostume();

        Costume base = costume.getExtendedFrom();
        this.labelExtendedFrom = new Label(base == null ? "None" : this.editor.resources.getCostumeResource(base).getName());

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
        this.form.grid.addRow("Extends", this.buttonExtendedFrom);

        Container all = new Container();
        all.setLayout(new VerticalLayout());
        this.notebook = new Notebook();

        Container eventsPage = new Container();
        Container propertiesPage = new Container();
        this.notebook.addPage("Events", eventsPage);
        this.notebook.addPage("Properties", propertiesPage);

        this.form.grid.addRow("", this.notebook);

        eventsPage.setLayout(new VerticalLayout());
        Container eventsTableSection = new Container();

        this.eventsTable = this.createEventsTable();
        this.eventsTable.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                CostumesEditor.this.onEditEvent();
            }
        });

        this.eventsTable.sort(0);
        eventsTableSection.addChild(this.eventsTable);
        eventsPage.addChild(eventsTableSection);
        Container eventsTableButtons = new Container();
        eventsTableButtons.setLayout(new VerticalLayout());
        eventsTableButtons.setYAlignment(0.5f);
        eventsTableSection.setFill(true, true);
        eventsTableButtons.addStyle("buttonColumn");

        eventsTableSection.addChild(eventsTableButtons);

        Button edit = new Button("Edit");
        edit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumesEditor.this.onEditEvent();
            }
        });
        eventsTableButtons.addChild(edit);

        Button add = new Button("Add");
        add.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumesEditor.this.onAddEvent();
            }
        });
        eventsTableButtons.addChild(add);

        Button remove = new Button("Remove");
        remove.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                CostumesEditor.this.onRemoveEvent();
            }
        });
        eventsTableButtons.addChild(remove);

        propertiesPage.setLayout(new VerticalLayout());

        this.propertiesClassName = new ClassNameBox(this.editor.getScriptManager(),
            costume.getPropertiesClassName(), CostumeProperties.class);

        this.propertiesClassName.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                ClassName className = CostumesEditor.this.propertiesClassName.getClassName();

                // if (className.equals(costume.getPropertiesClassName())) {
                // CostumesEditor.this.propertiesClassName.removeStyle("error");
                // } else {
                // Assume error...
                CostumesEditor.this.propertiesClassName.addStyle("error");

                if (CostumesEditor.this.editor.resources.checkClassName(className)) {

                    costume.setPropertiesClassName(CostumesEditor.this.editor.getScriptManager(), className);

                    createPropertiesGrid();
                    CostumesEditor.this.propertiesClassName.removeStyle("error");
                }
                // }
            }

        });

        propertiesPage.addChild(this.propertiesClassName);
        this.propertiesContainer = new Container();
        propertiesPage.addChild(this.propertiesContainer);
        createPropertiesGrid();

    }

    private void createPropertiesGrid()
    {
        CostumeProperties properties = this.currentResource.getCostume().getProperties();

        if (!this.currentResource.getCostume().getPropertiesClassName().name.equals(
            this.propertiesClassName.getClassName().name)) {

            properties = CostumeProperties.createProperties(this.editor.getScriptManager(),
                this.propertiesClassName.getClassName());
        }

        GridLayout grid = new GridLayout(this.propertiesContainer, 2);
        this.propertiesContainer.setLayout(grid);

        for (AbstractProperty<CostumeProperties, ?> property : properties.getProperties()) {

            try {
                Component component = property.createComponent(properties, true);
                grid.addRow(property.label, component);

            } catch (Exception e) {
                System.err.println("Failed to create component for Costume Property : " +
                    property.key);
                e.printStackTrace();
            }

        }

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

        Costume costume = this.currentResource.getCostume();

        for (String name : costume.getPoseNames()) {
            for (PoseResource poseResource : costume.getPoseChoices(name)) {

                if (!poseResource.isAnonymous()) {
                    SimpleTableModelRow row = new SimpleTableModelRow();
                    row.add(name);
                    row.add("Pose");
                    row.add(poseResource.getName());
                    row.add(poseResource);

                    model.addRow(row);
                }
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
                row.add("String");
                row.add(string);
                row.add(string);

                model.addRow(row);
            }
        }

        return model;
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
        String newValue = "?";
        this.currentResource.getCostume().addString(NEW_EVENT_NAME, newValue);
        rebuildEventTable();
        selectEventTableRow(NEW_EVENT_NAME, newValue);
        onEditEvent();
    }

    private void onAddPose()
    {
        PosePicker picker = new PosePicker(this.editor.resources) {
            @Override
            public void pick( PoseResource poseResource )
            {
                Costume costume = CostumesEditor.this.currentResource.getCostume();
                costume.addPose(NEW_EVENT_NAME, poseResource);
                CostumesEditor.this.rebuildEventTable();
                selectEventTableRow(NEW_EVENT_NAME, poseResource);
                onEditEvent();
            }
        };
        picker.show();
    }

    private HashMap<String, AnimationResource> createAnimationsHashMap()
    {
        HashMap<String, AnimationResource> animations = new HashMap<String, AnimationResource>();
        for (String name : this.editor.resources.animationNames()) {
            AnimationResource animationResource = this.editor.resources.getAnimationResource(name);
            animations.put(name, animationResource);
        }
        return animations;
    }

    private HashMap<String, SoundResource> createSoundsHashMap()
    {
        HashMap<String, SoundResource> sounds = new HashMap<String, SoundResource>();
        for (String name : this.editor.resources.soundNames()) {
            SoundResource soundResource = this.editor.resources.getSoundResource(name);
            sounds.put(name, soundResource);
        }
        return sounds;
    }

    private void onAddAnimation()
    {
        Picker<AnimationResource> picker = new Picker<AnimationResource>("Pick an Animation",
            createAnimationsHashMap()) {

            @Override
            public void pick( String label, AnimationResource animationResource )
            {
                Costume costume = CostumesEditor.this.currentResource.getCostume();
                costume.addAnimation(NEW_EVENT_NAME, animationResource);
                CostumesEditor.this.rebuildEventTable();
                selectEventTableRow(NEW_EVENT_NAME, animationResource);
                onEditEvent();
            }

        };
        picker.show();
    }

    private void onAddSound()
    {
        Picker<SoundResource> picker = new Picker<SoundResource>("Pick a Sound",
            createSoundsHashMap()) {

            @Override
            public void pick( String label, SoundResource soundResource )
            {
                Costume costume = CostumesEditor.this.currentResource.getCostume();
                ManagedSound ms = new ManagedSound(soundResource);
                costume.addSound(NEW_EVENT_NAME, ms);
                CostumesEditor.this.rebuildEventTable();
                selectEventTableRow(NEW_EVENT_NAME, ms);
                onEditEvent();
            }

        };
        picker.show();
    }

    private void onAddFont()
    {
        FontPicker picker = new FontPicker(this.editor.resources) {
            @Override
            public void pick( FontResource fontResource )
            {
                Costume costume = CostumesEditor.this.currentResource.getCostume();
                costume.addFont(NEW_EVENT_NAME, fontResource);
                CostumesEditor.this.rebuildEventTable();
                selectEventTableRow(NEW_EVENT_NAME, fontResource);
                onEditEvent();
            }
        };
        picker.show();
    }

    private void onRemoveEvent()
    {
        Costume costume = this.currentResource.getCostume();

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

            this.rebuildEventTable();
        }
    }

    private TextBox txtEventName;

    private TextBox txtEventString;

    private PosePickerButton eventPosePickerButton;

    private FontPickerButton eventFontPickerButton;

    private PickerButton<AnimationResource> eventAnimationPickerButton;

    private PickerButton<SoundResource> eventSoundPickerButton;

    private ManagedSound eventManagedSound;

    private void onEditEvent()
    {
        TableModelRow row = this.eventsTable.getCurrentTableModelRow();
        if (row == null) {
            return;
        }

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
                grid.addRow("Font", this.eventFontPickerButton);

            } else if (data instanceof AnimationResource) {
                this.eventAnimationPickerButton = new PickerButton<AnimationResource>(
                    "Pick an Animation",
                    (AnimationResource) data,
                    createAnimationsHashMap());
                grid.addRow("Animation", this.eventAnimationPickerButton);

            } else if (data instanceof ManagedSound) {

                this.eventManagedSound = (ManagedSound) data;
                this.eventSoundPickerButton = new PickerButton<SoundResource>(
                    "Pick a Sound",
                    this.eventManagedSound.soundResource,
                    createSoundsHashMap());
                grid.addRow("Sound", this.eventSoundPickerButton);

                for (AbstractProperty<ManagedSound, ?> property : this.eventManagedSound
                    .getProperties()) {
                    try {
                        Component component = property
                            .createComponent(this.eventManagedSound, true);
                        grid.addRow(property.label, component);
                    } catch (Exception e) {
                    }
                }

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
                    window.hide();
                }
            });
            Button cancel = new Button(new Label("Cancel"));
            cancel.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    window.hide();
                }
            });
            buttons.addChild(ok);
            buttons.addChild(cancel);

            window.clientArea.addChild(buttons);

            window.show();
        }
    }

    private void onEditEventOk()
    {
        Costume costume = this.currentResource.getCostume();
        TableModelRow row = this.eventsTable.getCurrentTableModelRow();

        if (row != null) {

            Object data = row.getData(EVENT_RESOURCE_COLUMN);
            onRemoveEvent();

            String name = this.txtEventName.getText();

            if (data instanceof String) {
                costume.addString(name, this.txtEventString.getText());

            } else if (data instanceof PoseResource) {
                costume.addPose(name, this.eventPosePickerButton.getValue());

            } else if (data instanceof FontResource) {
                costume.addFont(name, this.eventFontPickerButton.getValue());

            } else if (data instanceof AnimationResource) {
                costume.addAnimation(name, this.eventAnimationPickerButton.getValue());

            } else if (data instanceof ManagedSound) {
                this.eventManagedSound.soundResource = this.eventSoundPickerButton.getValue();
                costume.addSound(name, this.eventManagedSound);

            } else {
                System.err.println("Unexpected type in onEditEventOk : " +
                    data.getClass().getName());
            }
        }

        CostumesEditor.this.rebuildEventTable();

    }

    private void rebuildEventTable()
    {
        this.eventsTableModel = this.createEventsTableModel();
        this.eventsTable.setTableModel(this.eventsTableModel);
    }

    private void selectEventTableRow( String eventName, Object data )
    {
        for (int i = 0; i < this.eventsTableModel.getRowCount(); i++) {
            TableModelRow row = this.eventsTableModel.getRow(i);
            if ((row.getData(0).equals(eventName)) && (row.getData(EVENT_RESOURCE_COLUMN) == data)) {
                this.eventsTable.selectRow(row);
                return;
            }
        }
    }

    @Override
    protected void update() throws MessageException
    {
        TextBox name = (TextBox) this.form.getComponent("name");
        ClassNameBox role = (ClassNameBox) this.form.getComponent("roleClassName");

        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {
            if (this.editor.resources.getCostumeResource(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        if (!this.editor.resources.checkClassName(role.getClassName())) {
            throw new MessageException("Not a valid role class name");
        }

        if (!this.editor.resources.checkClassName(this.propertiesClassName.getClassName())) {

            throw new MessageException("Not a valid class name");
        }

        Costume oldBase = this.currentResource.getCostume().getExtendedFrom();
        if (this.labelExtendedFrom.getText().equals("None")) {
            this.currentResource.getCostume().setExtendedFrom(null);
        } else {
            Costume base = this.editor.resources.getCostume(this.labelExtendedFrom.getText());
            this.currentResource.getCostume().setExtendedFrom(base);
            Costume costume = base;
            for (int i = 0; i < 100; i++) {
                if (costume == null) {
                    break;
                }
                costume = costume.getExtendedFrom();
                if (i == 99) {
                    this.currentResource.getCostume().setExtendedFrom(oldBase);
                    throw new MessageException("Bad Extends - forms a loop.");
                }
            }
        }

        if (this.editor.resources.checkClassName(this.propertiesClassName.getClassName())) {

        } else {
            throw new MessageException("Invalid properties class name");
        }

        super.update();

        if (this.adding) {
            this.editor.resources.addCostume(this.currentResource);
        }
    }

    @Override
    protected void remove( CostumeResource costumeResource )
    {
        this.editor.resources.removeCostume(costumeResource.getName());
    }

    @Override
    protected void onAdd()
    {
        PoseOrFontPicker picker = new PoseOrFontPicker(this.editor.resources) {
            @Override
            public void pick( PoseResource poseResource )
            {
                CostumesEditor.this.add(poseResource);
            }

            @Override
            public void pick( FontResource fontResource )
            {
                CostumesEditor.this.add(fontResource);
            }
        };
        picker.show();
    }

    private void add( PoseResource poseResource )
    {
        Costume costume = new Costume();
        costume.addPose("default", poseResource);
        this.edit(new CostumeResource(this.editor.resources, poseResource.getName(), costume), true);
    }

    private void add( FontResource fontResource )
    {
        Costume costume = new Costume();
        costume.addFont("default", fontResource);
        costume.addString("default", fontResource.getName());

        this.edit(new CostumeResource(this.editor.resources, fontResource.getName(), costume), true);

    }

    @Override
    protected List<AbstractProperty<CostumeResource, ?>> getProperties()
    {
        return CostumeResource.properties;
    }
}
