package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ListNinePatches extends ListFileSubjects<NinePatch>
{

    public ListNinePatches(Resources resources)
    {
        super(resources);
    }

    @Override
    protected List<TableModelColumn> createTableColumns()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        TableModelColumn filename = new TableModelColumn("Filename", 1, 300);
        filename.rowComparator = new SingleColumnRowComparator<File>(1);

        TableModelColumn image= new TableModelColumn("Image", 2, 200) {
            @Override
            public AbstractComponent createCell( TableModelRow row )
            {
                return new ImageComponent((Surface) (row.getData(this.index)));
            }
        };
        
        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);
        columns.add(filename);
        columns.add(image);

        return columns;
        
    }

    @Override
    protected TableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String name : this.resources.ninePatchNames()) {
            NinePatch subject = this.resources.getNinePatch(name);
            String[] attributeNames = { "name", "file", "thumbnail" };
            TableModelRow row = new ReflectionTableModelRow<NinePatch>(subject, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    
    protected File getDirectory()
    {
        return resources.getImagesDirectory();
    }
    
    @Override
    protected void add(String name, File relativeFile)
        throws JameException
    {
        NinePatch ninePatch = new NinePatch();
        ninePatch.setName(name);
        ninePatch.setFile(relativeFile);
        EditNinePatch edit = new EditNinePatch(resources, ListNinePatches.this, ninePatch, true);
        edit.show();
    }
    
    protected void edit(NinePatch subject)
    {
        EditNinePatch edit = new EditNinePatch( this.resources, this, subject, false );
        edit.show();
    }

    @Override
    protected void remove(NinePatch subject)
    {
        this.resources.removeNinePatch(subject.getName());
    }

}
