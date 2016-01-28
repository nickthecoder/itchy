package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

public class ListInputs extends ListSubjects<Input>
{

    public ListInputs(Resources resources)
    {
        super(resources);
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn keys = new TableModelColumn("Keys", 1, 300);
        keys.rowComparator = new SingleColumnRowComparator<String>(1);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(keys);

        return columns;
        
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String inputName : this.resources.inputNames()) {
            Input input = this.resources.getInput(inputName);
            String[] attributeNames = { "name", "keysString" };
            TableModelRow row = new ReflectionTableModelRow<Input>(input, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void addOrEdit(Input subject)
    {
        boolean isNew = false;
        if (subject == null) {
            subject = new Input();
            isNew = true;
        }
        
        EditInput editInput = new EditInput( this.resources, this, subject, isNew );
        editInput.show();
    }

    @Override
    protected void remove(Input subject)
    {
        this.resources.removeInput(subject.getName());
    }

}
