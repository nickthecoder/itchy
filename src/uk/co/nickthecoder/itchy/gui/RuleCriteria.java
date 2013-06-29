package uk.co.nickthecoder.itchy.gui;

public class RuleCriteria
{
    public String type;
    public String style;
    public boolean wildcard;

    public RuleCriteria( String type, String style )
    {
        this.type = type;
        this.style = style;
        this.wildcard = false;
    }

    public RuleCriteria( boolean wildcard )
    {
        this.wildcard = wildcard;
        this.type = null;
        this.style = null;
    }

    @Override
    public String toString()
    {
        if ( this.wildcard ) {
            return "*";
        }
        if ( this.type == null ) {
            if ( this.style == null ) {
                return "?";
            }
            return "." + this.style;
        }
        if ( this.style == null ) {
            return this.type;
        } else {
            return this.type + "." + this.style;
        }
    }
}
