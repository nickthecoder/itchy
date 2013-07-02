package uk.co.nickthecoder.itchy;

public class SceneResource extends NamedResource
{
    private String filename;

    private Scene scene;

    public SceneResource( Resources resources, String name, String filename )
    {
        super(resources, name);
        this.filename = filename;
        this.scene = null;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public final void setFilename( String filename )
    {
        this.filename = filename;
    }

    public Scene getScene() throws Exception
    {
        if (this.scene == null) {
            SceneReader sceneReader = new SceneReader(this.resources);
            this.scene = sceneReader.load(this.resources.resolveFilename(this.filename));
        }
        return this.scene;
    }

    public void setScene( Scene scene )
    {
        this.scene = scene;
    }

    public void save() throws Exception
    {
        try {
            this.getScene();
        } catch (Exception e) {
            this.scene = new Scene();
        }

        SceneWriter sceneWriter = new SceneWriter(this);
        sceneWriter.write(this.resources.resolveFilename(this.filename));
    }

}
