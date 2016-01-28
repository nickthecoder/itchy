package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

public class ListLayers extends ListSubjects<Layer>
{
    private Layout layout;

    public ListLayers(Resources resources, Layout layout)
    {
        super(resources);
        this.layout = layout;
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
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

        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (Layer layer : this.layout.getLayers()) {
            String[] attributeNames = { "name", "zOrder", "position.x", "position.y", "position.width",
                "position.height" };
            TableModelRow row = new ReflectionTableModelRow<Layer>(layer, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void addOrEdit(Layer subject)
    {
        boolean isNew = false;
        if (subject == null) {
            subject = new Layer();
            isNew = true;
        }
        
        EditLayer edit = new EditLayer( this.resources, this, this.layout, subject, isNew );
        edit.show();    
    }

    @Override
    protected void remove(Layer subject)
    {
        this.layout.removeLayer(subject);
    }

}
