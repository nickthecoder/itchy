package uk.co.nickthecoder.itchy;

/**
 * Used by roles that need to keep track of the position of the mouse pointer. Typically this is the player's
 * character.
 * 
 * Note, if you want an actor to follow the mouse around the screen, consider using SimpleMousePointer instead,
 * because this will automatically take care of hiding the special pointer when the mouse leaves the game's window.
 * See Destroy-Debris's director.py for an example of using SimpleMousePointer.
 * 
 * Scripting languages such as Jython cannot specify which interfaces a class implements, therefore
 * a game script cannot just extend AbstractRole and implement ViewMouseListener. Instead, they can
 * extend this class.
 * 
 */
public abstract class AbstractMouseRole extends AbstractRole implements ViewMouseListener
{
    @Override
    public boolean isMouseListener()
    {
        return true;
    }
}
