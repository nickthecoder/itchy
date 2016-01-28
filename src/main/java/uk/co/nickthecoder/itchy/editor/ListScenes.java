package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneStub;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;

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
            scene = new Scene();
            scene.layout = resources.getLayout("default");
            if (scene.layout == null) {
                scene.layout = new Layout();
            }
        } else {
            try {
                scene = subject.load();
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

}
