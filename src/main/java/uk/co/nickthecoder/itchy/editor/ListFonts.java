package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.MessageDialog;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

public class ListFonts extends ListFileSubjects<Font>
{

    public ListFonts(Resources resources)
    {
        super(resources);
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<String>(1);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        
        return columns;
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String fontName : this.resources.fontNames()) {
            Font font = this.resources.getFont(fontName);
            String[] attributeNames = { "name", "file" };
            TableModelRow row = new ReflectionTableModelRow<Font>(font, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void add(String name, File relativeFile)
    {
        Font font = new Font();
        font.setName( name );
        font.setFile(relativeFile);
        EditFont edit = new EditFont( this.resources, this, font, false );
        edit.show();        
    }

    protected File getDirectory()
    {
        return resources.getFontsDirectory();
    }
    
    @Override
    protected void edit(Font subject)
    {
        EditFont edit = new EditFont( this.resources, this, subject, false );
        edit.show();        
    }


    @Override
    protected void remove( Font font)
    {
        MessageBox messageBox = new MessageBox("Checking All Scenes", "This may take a while");
        messageBox.showNow();

        String usedBy = this.resources.used( font );
        
        messageBox.hide();
        
        if (usedBy != null) {
            MessageDialog message = new MessageDialog("Cannot Remove", "This pose is being used by : \n\n" + usedBy );
            message.show();
        } else {
            this.resources.removeFont(font.getName());
        }
    }

}
