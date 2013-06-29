package uk.co.nickthecoder.itchy.util;

public class WorldRectangle
{
    public double x;
    public double y;
    public double width;
    public double height;

    public WorldRectangle( double x, double y, double width, double height )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean overlaps( WorldRectangle other )
    {
        if ( this.x > other.x + other.width ) {
            return false;
        }
        if ( this.y > other.y + other.height ) {
            return false;
        }
        if ( other.x > this.x + this.width ) {
            return false;
        }
        if ( other.y > this.y + this.height ) {
            return false;
        }
        return true;
    }

    public boolean within( WorldRectangle other )
    {
        if ( (this.x < other.x) || (this.y < other.y) ) {
            return false;
        }

        if ( this.x + this.width > other.x + other.width ) {
            return false;
        }
        if ( this.y + this.height > other.y + other.height ) {
            return false;
        }
        return true;
    }

    public boolean contains( double x, double y )
    {
        return ( (x >= this.x) && (y >= this.y) && (x < this.x + this.width) && (y < this.y + this.height) );
    }

    @Override
    public String toString()
    {
        return "WR(" + this.x + "," + this.y + ", " + this.width + "," + this.height + ")";
    }
}
