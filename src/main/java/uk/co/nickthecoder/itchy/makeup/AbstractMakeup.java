package uk.co.nickthecoder.itchy.makeup;

import uk.co.nickthecoder.itchy.util.ClassName;

public abstract class AbstractMakeup implements Makeup
{
    public ClassName getClassName()
    {
        return new ClassName( Makeup.class, this.getClass().getName() );
    }

}
