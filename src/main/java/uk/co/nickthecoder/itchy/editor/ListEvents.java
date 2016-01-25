package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Costume.Event;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.ManagedSound;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SoundResource;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.AnimationPicker;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.CostumePicker;
import uk.co.nickthecoder.itchy.gui.FontPicker;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.Picker;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PoseResourcePicker;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.SoundPicker;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

public class ListEvents extends ListSubjects<Costume.Event>
{

    private Costume costume;

    public ListEvents(Resources resources, Costume costume)
    {
        super(resources);
        this.costume = costume;
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn eventColumn = new TableModelColumn("Event", 0, 200);
        eventColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn typeColumn = new TableModelColumn("Type", 1, 100);
        typeColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn nameColumn = new TableModelColumn("Resource", 2, 200);
        nameColumn.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn previewColumn = new TableModelColumn("", 3, 140)
        {
            public void addPlainCell(Container container, final TableModelRow r)
            {
                @SuppressWarnings("rawtypes")
                ReflectionTableModelRow row = (ReflectionTableModelRow) r;

                Event event = (Event) row.getData();
                final Object data = event.data;

                if (data instanceof String) {
                    container.addChild(new Label((String) data));
                } else if (data instanceof PoseResource) {
                    PoseResource resource = (PoseResource) data;
                    container.addChild(new ImageComponent(resource.getThumbnail()));
                } else if (data instanceof Costume) {
                    Costume companion = (Costume) data;
                    container.addChild(new ImageComponent(companion.getThumbnail()));
                } else if (data instanceof ManagedSound) {
                    GuiButton button = new GuiButton("Play");
                    button.addActionListener(new ActionListener()
                    {
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
            public AbstractComponent createCell(TableModelRow row)
            {
                PlainContainer container = new PlainContainer();
                this.addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent(Component component, TableModelRow row)
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

        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        String[] attributeNames = { "eventName", "type", "resourceName" };
        for (String eventName : costume.getCompanionNames()) {
            for (Costume companion : costume.getCompanionChoices(eventName)) {

                Costume.Event event = new Costume.Event(costume, eventName, companion, "Companion");
                TableModelRow row = new ReflectionTableModelRow<Event>(event, attributeNames);
                model.addRow(row);
            }
        }
        for (String eventName : costume.getPoseNames()) {
            for (PoseResource poseResource : costume.getPoseChoices(eventName)) {

                Costume.Event event = new Costume.Event(costume, eventName, poseResource, "Pose");
                TableModelRow row = new ReflectionTableModelRow<Event>(event, attributeNames);
                model.addRow(row);

            }
        }
        for (String eventName : costume.getAnimationNames()) {
            for (AnimationResource animationResource : costume.getAnimationChoices(eventName)) {

                Costume.Event event = new Costume.Event(costume, eventName, animationResource, "Animation");
                TableModelRow row = new ReflectionTableModelRow<Event>(event, attributeNames);
                model.addRow(row);

            }
        }
        for (String eventName : costume.getSoundNames()) {
            for (ManagedSound costumeSound : costume.getSoundChoices(eventName)) {

                Costume.Event event = new Costume.Event(costume, eventName, costumeSound, "Sound");
                TableModelRow row = new ReflectionTableModelRow<Event>(event, attributeNames);
                model.addRow(row);
            }
        }
        for (String eventName : costume.getTextStyleNames()) {
            for (TextStyle textStyle : costume.getTextStyleChoices(eventName)) {

                Costume.Event event = new Costume.Event(costume, eventName, textStyle, "Text Style");
                TableModelRow row = new ReflectionTableModelRow<Event>(event, attributeNames);
                model.addRow(row);

            }
        }
        for (String eventName : costume.getStringNames()) {
            for (String string : costume.getStringChoices(eventName)) {

                Costume.Event event = new Costume.Event(costume, eventName, string, "String");
                TableModelRow row = new ReflectionTableModelRow<Event>(event, attributeNames);
                model.addRow(row);
            }
        }

        return model;
    }

    @Override
    protected void remove(Event subject)
    {
        Object data = subject.data;

        if (data instanceof PoseResource) {
            costume.removePose(subject.eventName, (PoseResource) data);
        } else if (data instanceof AnimationResource) {
            costume.removeAnimation(subject.eventName, (AnimationResource) data);
        } else if (data instanceof Costume) {
            costume.removeCompanion(subject.eventName, (Costume) data);
        } else if (data instanceof ManagedSound) {
            costume.removeSound(subject.eventName, (ManagedSound) data);
        } else if (data instanceof TextStyle) {
            costume.removeTextStyle(subject.eventName, (TextStyle) data);
        }
    }

    @Override
    protected void edit(Event subject)
    {
        if (subject == null) {
            add();
            return;
        }

        EditEvent edit = new EditEvent(resources, this, subject, false);
        edit.show();
    }

    private void add()
    {
        HashMap<String, Class<?>> pickList = new HashMap<String, Class<?>>();

        pickList.put("String", String.class);
        pickList.put("Pose", PoseResource.class);
        pickList.put("Animation", AnimationResource.class);
        pickList.put("Sound", ManagedSound.class);
        pickList.put("Font", TextStyle.class);
        pickList.put("Companion", Costume.class);

        Picker<Class<?>> picker = new Picker<Class<?>>("Which Type?", pickList)
        {
            @Override
            public void pick(String string, Class<?> picked)
            {
                if (picked == String.class) {
                    addString();

                } else if (picked == PoseResource.class) {
                    addPose();

                } else if (picked == AnimationResource.class) {
                    addAnimation();

                } else if (picked == ManagedSound.class) {
                    addSound();

                } else if (picked == TextStyle.class) {
                    addTextStyle();

                } else if (picked == Costume.class) {
                    addCompanion();
                }
            }
        };
        picker.show();
    }

    private static final String NEW_EVENT_NAME = "x";

    private Event selectRow(String eventName, Object data)
    {
        for (int i = 0; i < table.getTableModel().getRowCount(); i++) {
            ReflectionTableModelRow<?> row = (ReflectionTableModelRow<?>) table.getTableModel().getRow(i);
            Event event = (Event) row.getData();
            if ((row.getData(0).equals(eventName)) && (event.data == data)) {
                table.selectRow(row);
                return event;
            }
        }
        return null;
    }

    private void added(Object data)
    {
        rebuildTable();
        Event event = selectRow(NEW_EVENT_NAME, data);
        if (event != null) {
            edit(event);
        }
    }

    private void addString()
    {
        String newValue = "?";
        costume.addString(NEW_EVENT_NAME, newValue);
        added(newValue);
    }

    private void addCompanion()
    {
        CostumePicker picker = new CostumePicker(resources)
        {
            @Override
            public void pick(Costume companion)
            {
                costume.addCompanion(NEW_EVENT_NAME, companion);
                added(costume);
            }
        };
        picker.show();
    }

    private void addPose()
    {
        PoseResourcePicker picker = new PoseResourcePicker(resources)
        {
            @Override
            public void pick(PoseResource poseResource)
            {
                costume.addPose(NEW_EVENT_NAME, poseResource);
                added(poseResource);
            }
        };
        picker.show();
    }

    private void addAnimation()
    {
        AnimationPicker picker = new AnimationPicker()
        {
            @Override
            public void pick(String label, AnimationResource animationResource)
            {
                costume.addAnimation(NEW_EVENT_NAME, animationResource);
                added(animationResource);
            }
        };
        picker.show();
    }

    private void addSound()
    {
        SoundPicker picker = new SoundPicker()
        {
            @Override
            public void pick(String label, SoundResource soundResource)
            {
                ManagedSound ms = new ManagedSound(soundResource);
                costume.addSound(NEW_EVENT_NAME, ms);
                added(ms);
            }
        };
        picker.show();
    }

    private void addTextStyle()
    {
        FontPicker picker = new FontPicker(resources)
        {
            @Override
            public void pick(Font font)
            {
                TextStyle textStyle = new TextStyle(font, 14);
                costume.addTextStyle(NEW_EVENT_NAME, textStyle);
                added(textStyle);
            }
        };
        picker.show();
    }

}
