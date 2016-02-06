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

    public Scene load( boolean design ) throws Exception
    {
        Scene scene = this.load(Itchy.getGame().resources.resolveFile(getFile()), design);
        scene.name = this.name;
        return scene;
    }

    private Scene load(File file, boolean design) throws Exception
    {
        SceneReader reader = new SceneReader(Itchy.getGame().resources, design);
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

    // TODO Implement SceneStub checkSave
    private void checkSave(Scene scene, File file) throws Exception
    {
    }
}
