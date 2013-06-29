package uk.co.nickthecoder.itchy;

public class NamedResource
{
    protected Resources resources;

    protected String name;

    public NamedResource( Resources resources, String name )
    {
        this.resources = resources;
        this.name = name;
    }

    public void rename( String newName )
    {
        this.resources.rename( this, newName );
        this.name = newName;
    }

    public String getName()
    {
        return this.name;
    }
}
