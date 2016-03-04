package uk.co.nickthecoder.itchy.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneStub;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.util.Util;

public class ListScenes extends ListSubjects<SceneStub>
{

    private Editor editor;
    
    public ListScenes(Editor editor)
    {
        super(editor.resources);
        this.editor = editor;
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

        for (String sceneName : this.editor.resources.sceneNames()) {
            SceneStub sceneStub = this.editor.resources.getScene(sceneName);
            String[] attributeNames = { "name" };
            TableModelRow row = new ReflectionTableModelRow<SceneStub>(sceneStub, attributeNames);
            model.addRow(row);
        }
        return model;
    }

    @Override
    protected void addOrEdit(SceneStub subject)
    {
        Scene scene;
        
        if (subject == null) {
            subject = new SceneStub();
            subject.setName( "new" );
            scene = new Scene();
            scene.layout = resources.getLayout("default");
            if (scene.layout == null) {
                try {
                    scene.layout = resources.getLayout( resources.layoutNames().get(0) );
                } catch (Exception e) {
                    // TODO Show message "You need to create a default Layout".
                    e.printStackTrace();
                }
            }
        } else {
            try {
                scene = subject.load( true );
            } catch (Exception e) {
                Itchy.handleException(e);
                return;
            }
        }
        this.editor.sceneDesigner = new SceneDesigner(this.editor, subject, scene);
        
        this.editor.root.hide();
        this.editor.sceneDesigner.go();
    }

    @Override
    protected void remove(SceneStub subject)
    {
        subject.delete();
        this.resources.removeScene(subject.getName());
    }
    
    protected void addListButtons(Container buttonBar)
    {
        Button duplicate = new Button("Duplicate");
        duplicate.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onDuplicate();
            }
        });
        buttonBar.addChild(duplicate);
        super.addListButtons(buttonBar);
    }
    
    private void onDuplicate()
    {
        SceneStub oldStub = getCurrentItem();
        
        String newName = null;
        if (oldStub != null) {
            for ( int i = 1; i < 100; i ++ ) {
                newName = oldStub.getName() + " copy#" + i;
                if (resources.getScene(newName) == null) {
                    break;
                }
            }
            if (newName == null) {
                return;
            }
            
            SceneStub newStub = new SceneStub();
            newStub.setName(newName);
            File oldFile = oldStub.getFile();
            File newFile = newStub.getFile();
            try {
                Util.copyFile(oldFile,newFile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            resources.addScene(newStub);
            this.rebuildTable();
            this.selectItem(newStub);
            this.onEdit();
        }
    }

}
