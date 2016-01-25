package uk.co.nickthecoder.itchy;

import java.util.LinkedList;
import java.util.List;

import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.PropertySubject;
import uk.co.nickthecoder.jame.Surface;

public class Sprite extends PoseResource implements Comparable<Sprite>, PropertySubject<Sprite> 
{
    protected static List<Property<Sprite, ?>> properties = new LinkedList<Property<Sprite, ?>>();

    
    static {
        Property.addAll( PoseResource.properties, properties );
        properties.add( new IntegerProperty<Sprite>( "x" ) );
        properties.add( new IntegerProperty<Sprite>( "y" ) );
        properties.add( new IntegerProperty<Sprite>( "width" ) );
        properties.add( new IntegerProperty<Sprite>( "height" ) );
    }

    @Override
    public List<Property<Sprite, ?>> getProperties()
    {
        return properties;
    }

    private SpriteSheet spriteSheet;
    
    private int x;

    private int y;
    
    private int width = 1;
    
    private int height = 1;
    
    public Sprite(SpriteSheet spriteSheet, String name)
    {
        super(name);
        this.spriteSheet = spriteSheet;
        this.pose = new ImagePose(createSurface());
    }

    private final Surface createSurface()
    {
        Surface result = new Surface( width, height, true );
        spriteSheet.getSurface().blit(result, -x, -y, Surface.BlendMode.COMPOSITE);
        
        return result;
    }
    
    public int getX()
    {
        return this.x;
    }
    
    public void setX( int x )
    {
        setBounds( x, this.y, this.width, this.height );
    }
    
    public int getY()
    {
        return this.y;
    }
    
    public void setY( int y )
    {
        setBounds( this.x, y, this.width, this.height );
    }

    public int getWidth()
    {
        return this.width;
    }
    
    public void setWidth( int width )
    {
        setBounds( this.x, this.y, width, this.height );
    }
    
    public int getHeight()
    {
        return this.height;
    }
    
    public void setHeight( int height )
    {
        setBounds( this.x, this.y, this.width, height );
    }
    
    public void setBounds( int x, int y, int width, int height )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        ImagePose newPose = new ImagePose( createSurface(), this.pose.getOffsetX(), this.pose.getOffsetY() );
        newPose.setDirection(this.pose.getDirection());
        this.pose = newPose;
        this.resetThumbnail();
    }
    
    @Override
    public int compareTo(Sprite o)
    {
        return this.name.compareTo( o.name );
    }
    
}
