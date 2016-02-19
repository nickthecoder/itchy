package uk.co.nickthecoder.itchy.remote;

import uk.co.nickthecoder.jame.Rect;

public class ViewBounds
{
    public int ox;
    public int oy;
    public Rect clip = new Rect(0,0,1,1);
    
    public boolean isEquals( Object o )
    {
        if (o instanceof ViewBounds) {
            ViewBounds other = (ViewBounds) o;
            return ((this.ox == other.ox) && (this.oy == other.oy)&& (this.clip.equals( other.clip)) );
        }
        return false;
    }
}
