package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.ViewMouseListener;

/**
 * This class exists because there isn't a way to declare a Jython class as implmenting an interface.
 */
public abstract class MouseRole extends AbstractRole implements ViewMouseListener
{
    public boolean isMouseListener()
    {
        return true;
    }

}
