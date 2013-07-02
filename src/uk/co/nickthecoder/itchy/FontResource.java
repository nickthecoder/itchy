package uk.co.nickthecoder.itchy;

public class FontResource extends NamedResource
{
    public String filename;

    public Font font;

    public FontResource( Resources resources, String name, String filename )
    {
        super(resources, name);
        this.filename = filename;
        this.font = new Font(this.resources.resolveFilename(filename));
    }

    public void setFilename( String filename )
    {
        this.font = new Font(this.resources.resolveFilename(filename));
        this.filename = filename;
    }
}
