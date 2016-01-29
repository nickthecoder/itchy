package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.PoseOrFontPicker;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.WrappedRowComparator;
import uk.co.nickthecoder.jame.Surface;

public class ListCostumes extends ListSubjects<Costume>
{

    public ListCostumes(Resources resources)
    {
        super(resources);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn order = new TableModelColumn("Order", 1, 80);
        // Allow sorting by the costume resources's order, and then its name.
        @SuppressWarnings("rawtypes")
        Comparator comparator = (Costume.orderComparator);
        order.rowComparator = new WrappedRowComparator(4, (Comparator<Object>) comparator);

        TableModelColumn zOrder = new TableModelColumn("Z Order", 2, 90);
        zOrder.rowComparator = new SingleColumnRowComparator<String>(2);

        TableModelColumn extendedFrom = new TableModelColumn("Extends", 3, 200);
        extendedFrom.rowComparator = new SingleColumnRowComparator<String>(3);


        TableModelColumn image = new TableModelColumn("\"default\" Pose", 0, 150)
        {
            public AbstractComponent createLabelOrImage(TableModelRow r)
            {
                @SuppressWarnings("rawtypes")
                ReflectionTableModelRow row = (ReflectionTableModelRow) r;
                Costume costume = (Costume) row.getData();

                Surface surface = resources.getThumbnail(costume);
                if (surface == null) {
                    return new Label("none");
                } else {
                    return new ImageComponent(surface);
                }
            }

            @Override
            public AbstractComponent createCell(TableModelRow row)
            {
                PlainContainer container = new PlainContainer();
                container.addChild(this.createLabelOrImage(row));
                return container;
            }

            @Override
            public void updateComponent(Component component, TableModelRow row)
            {
                Container container = (Container) component;
                container.clear();
                container.addChild(this.createLabelOrImage(row));
            }
        };

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(order);
        columns.add(image);
        columns.add(zOrder);
        columns.add(extendedFrom);

        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        String[] attributeNames = { "name", "order", "defaultZOrder", "extendedFromName", null };
        for (String costumeName : this.resources.costumeNames()) {
            Costume costume = this.resources.getCostume(costumeName);
            TableModelRow row = new ReflectionTableModelRow<Costume>(costume, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    private PoseOrFontPicker poseOrFontPicker;

    @Override
    protected void addOrEdit(Costume subject)
    {
        if (subject == null) {
            final Costume newCostume = new Costume();

            poseOrFontPicker = new PoseOrFontPicker(resources)
            {
                @Override
                public void pick(PoseResource poseResource)
                {
                    newCostume.addPose("default", poseResource);
                    newCostume.setName(poseResource.getName());
                    add(newCostume);
                }

                @Override
                public void pick(Font fontResource)
                {
                    TextStyle textStyle = new TextStyle(fontResource, 14);
                    newCostume.addTextStyle("default", textStyle);
                    newCostume.setName(textStyle.getFont().getName());
                    add(newCostume);
                }

            };
            poseOrFontPicker.show();

        } else {

            EditCostume edit = new EditCostume(this.resources, this, subject, false);
            edit.show();
        }
    }

    protected void add(Costume subject)
    {
        EditCostume edit = new EditCostume(this.resources, this, subject, true);
        edit.show();
    }

    @Override
    protected void remove(Costume subject)
    {
        this.resources.removeCostume(subject.getName());
    }

}
