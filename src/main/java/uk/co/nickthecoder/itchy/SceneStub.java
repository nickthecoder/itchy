package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.StringProperty;
import uk.co.nickthecoder.itchy.util.SaveBackup;

/**
 * Contains basic information about a Scene, without loading it.
 * The fully loaded scene is held in the Scene class.
 */
public class SceneStub implements NamedSubject<SceneStub>
{
    protected static final List<Property<SceneStub, ?>> properties = new ArrayList<Property<SceneStub, ?>>();

    static {
        properties.add(new StringProperty<SceneStub>("name"));
    }

    @Override
    public List<Property<SceneStub, ?>> getProperties()
    {
        return properties;
    }

    private String name;

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String newName)
    {
        // Don't attempt to rename when we are setting the name for the first time.
        if (this.name == null) {
            this.name = newName;
            return;
        }

        if (!this.name.equals(newName)) {
            File oldFile = this.getFile();
            File newFile = getFile(newName);

            oldFile = Itchy.getGame().resources.resolveFile(oldFile);
            newFile = Itchy.getGame().resources.resolveFile(newFile);

            if (newFile.exists()) {
                throw new RuntimeException("File already exists");
            }
            oldFile.renameTo(newFile);

        }
        this.name = newName;
    }

    public File getFile()
    {
        return getFile(this.name);
    }

    private File getFile(String name)
    {
        File folder = Itchy.getGame().resources.resolveFile(new File("scenes"));
        return new File(folder, name + ".xml");
    }

    public boolean isValidName(String newName)
    {
        if ((newName == null) || (newName.equals("")) || (newName.contains(".."))) {
            return false;
        }

        File raw = new File( newName );
        if (raw.isAbsolute()) {
            return false;
        }
        
        if (newName.equals(this.name)) {
            return true;
        }

        File file = getFile( newName );
        if (file.exists()) {
            return false;
        }
        return true;
    }

    public Scene load() throws Exception
    {
        Scene scene = this.load(Itchy.getGame().resources.resolveFile(getFile()));
        scene.name = this.name;
        System.out.println(scene.layout);
        return scene;
    }

    private Scene load(File file) throws Exception
    {
        SceneReader reader = new SceneReader(Itchy.getGame().resources);
        return reader.load(file);

    }

    public void save(Scene scene) throws Exception
    {
        SaveBackup saveBackup = new SaveBackup(this.getFile());

        SceneWriter sceneWriter = new SceneWriter(Itchy.getGame().resources, scene);
        sceneWriter.write(saveBackup.getTemporyFile());

        this.checkSave(scene, saveBackup.getTemporyFile());
        saveBackup.complete();
    }
    
    public void delete()
    {
        SaveBackup saveBackup = new SaveBackup(this.getFile());
        saveBackup.renameToBackup();
    }

    // TODO This needs looking at - more things to check.
    private void checkSave(Scene scene, File file) throws Exception
    {
        Scene newScene = this.load(file);

        List<Scene.SceneLayer> newSceneLayers = newScene.getSceneLayers();

        int i = 0;
        for (Scene.SceneLayer oldSceneLayer : scene.getSceneLayers()) {
            if (i >= newSceneLayers.size()) {
                if (oldSceneLayer.getSceneActors().size() != 0) {
                    throw new Exception("Layers differ");
                }
                continue;
            }
            Scene.SceneLayer newSceneLayer = newSceneLayers.get(i);
            if (!oldSceneLayer.getName().equals(newSceneLayer.getName())) {
                if (oldSceneLayer.getSceneActors().size() == 0) {
                    continue;
                }
            }
            i++;
            SaveBackup.ensure(oldSceneLayer.name, newSceneLayer.name, "Different stage name");

            List<SceneActor> newSceneActors = newSceneLayer.getSceneActors();
            SaveBackup.ensure(newSceneActors.size(), oldSceneLayer.getSceneActors().size(),
                "Different number of actors");

            int j = 0;
            for (SceneActor oldSceneActor : oldSceneLayer.getSceneActors()) {
                SceneActor newSceneActor = newSceneActors.get(j);
                j++;
                SaveBackup.ensure(oldSceneActor, newSceneActor, "Different actor #" + j);
            }

        }
    }
}
