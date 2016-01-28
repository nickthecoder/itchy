package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneStub;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.util.StringList;

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
        StringList usedBy = new StringList();

        for (String costumeName : this.resources.costumeNames()) {
            Costume costume = this.resources.getCostume(costumeName);
            for (String resourceName : costume.getTextStyleNames()) {
                for (TextStyle ts : costume.getTextStyleChoices(resourceName)) {
                    if (ts.getFont() == font) {
                        usedBy.add(costumeName);
                    }
                }
            }
        }
        if (usedBy.isEmpty()) {
            if (!usedInScenes(font)) {
                this.resources.removeFont(font.getName());        
            }
        } else {
            new MessageBox("Cannot Delete. Used by Costumes...", usedBy.toString()).show();
        }
    }

    private boolean usedInScenes( Font font)
    {
        StringList list = new StringList();

        MessageBox messageBox = new MessageBox("Checking All Scenes", "This may take a while");
        messageBox.showNow();

        try {
            Resources resources = this.resources;
            for (String sceneName : resources.sceneNames()) {
                try {
                    SceneStub stub = resources.getScene(sceneName);
                    Scene scene = stub.load();
                    if (scene.uses(font)) {
                        list.add(sceneName);
                    }
                } catch (Exception e) {
                    list.add(sceneName + " (failed to load)");
                }
            }

        } finally {
            messageBox.hide();
        }

        if (!list.isEmpty()) {
            new MessageBox("Cannot Delete. Used in scenes...", list.toString()).show();
        }

        return !list.isEmpty();
    }

}
