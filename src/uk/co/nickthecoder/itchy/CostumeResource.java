package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.Surface;

public class CostumeResource extends NamedResource
{

    public Costume costume;

    public CostumeResource( Resources resources, String name, Costume costume )
    {
        super( resources, name );
        this.costume = costume;
    }

    public String getExtendedFromName()
    {
        Costume base = costume.getExtendedFrom();
        if ( base == null ) {
            return null;
        } else {
            return this.resources.getCostumeName( base );
        }
    }

    public Surface getThumbnail()
    {
        return this.resources.getThumbnail( this );
    }
}