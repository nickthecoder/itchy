package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
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

        TableModelColumn extendedFrom = new TableModelColumn("Extends", 1, 200);
        extendedFrom.rowComparator = new SingleColumnRowComparator<String>(2);

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

        TableModelColumn order = new TableModelColumn("Order", 3, 80);
        // Allow sorting by the costume resources's order, and then its name.
        @SuppressWarnings("rawtypes")
        Comparator comparator = (Costume.orderComparator);
        order.rowComparator = new WrappedRowComparator(4, (Comparator<Object>) comparator);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(order);
        columns.add(extendedFrom);
        columns.add(image);
        
        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        String[] attributeNames = { "name", "extendedFromName", "order", null };
        for (String costumeName : this.resources.costumeNames()) {
            Costume costume = this.resources.getCostume(costumeName);
            TableModelRow row = new ReflectionTableModelRow<Costume>(costume, attributeNames);
            model.addRow(row);
        }
        return model;
    }
    
    @Override
    protected void edit(Costume subject)
    {
        boolean isNew = false;
        if (subject == null) {
            subject = new Costume();
            isNew = true;
        }
        
        EditCostume edit = new EditCostume( this.resources, this, subject, isNew );
        edit.show();        
    }

    @Override
    protected void remove(Costume subject)
    {
        this.resources.removeCostume(subject.getName());                
    }

}
