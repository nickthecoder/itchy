package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SpriteSheet;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.FileOpenDialog;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.util.Util;
import uk.co.nickthecoder.jame.JameException;
import uk.co.nickthecoder.jame.Surface;

public class ListSpriteSheets extends ListSubjects<SpriteSheet>
{

    public ListSpriteSheets(Resources resources)
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

        TableModelColumn image = new TableModelColumn("Image", 2, 100)
        {
            @Override
            public AbstractComponent createCell(TableModelRow row)
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
        for (String spriteSheetName : this.resources.spriteSheetNames()) {
            SpriteSheet spriteSheet = this.resources.getSpriteSheet(spriteSheetName);
            String[] attributeNames = { "name", "file", "thumbnail" };
            TableModelRow row = new ReflectionTableModelRow<SpriteSheet>(spriteSheet, attributeNames);
            model.addRow(row);
        }
        return model;
    }
    
    protected FileOpenDialog openDialog;

    @Override
    protected void edit(SpriteSheet subject)
    {
        if (subject == null) {
            this.openDialog = new FileOpenDialog()
            {
                @Override
                public void onChosen(File file)
                {
                    File relative = resources.makeRelativeFile(file);
                    String name = Util.nameFromFile(file);
                    try {
                        SpriteSheet spriteSheet = new SpriteSheet(name, relative);
                        EditSpriteSheet edit = new EditSpriteSheet(resources, ListSpriteSheets.this, spriteSheet, true);
                        edit.show();

                        openDialog.hide();

                    } catch (JameException e) {
                        openDialog.setMessage(e.getMessage());
                        return;
                    }

                }
            };
            this.openDialog.setDirectory(resources.getImageDirectory());
            this.openDialog.show();

            return;
        }
        
        EditSpriteSheet edit = new EditSpriteSheet( this.resources, this, subject, false );
        edit.show();          
    }

    @Override
    protected void remove(SpriteSheet subject)
    {
        this.resources.removeSpriteSheet(subject.getName());        
    }

}
