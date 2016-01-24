package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

public class ListLayouts extends ListSubjects<Layout>
{

    public ListLayouts(Resources resources)
    {
        super(resources);
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {

        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);

        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String name : this.resources.layoutNames()) {
            Layout layout = this.resources.getLayout(name);
            String[] attributeNames = { "name" };
            TableModelRow row = new ReflectionTableModelRow<Layout>(layout, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void edit(Layout subject)
    {
        boolean isNew = false;
        if (subject == null) {
            subject = new Layout();
            isNew = true;
        }
        
        EditLayout edit = new EditLayout( this.resources, this, subject, isNew );
        edit.show();            
    }

    @Override
    protected void remove(Layout subject)
    {
        this.resources.removeLayout(subject.getName());
    }

}
