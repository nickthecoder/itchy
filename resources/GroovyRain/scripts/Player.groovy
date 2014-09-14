import uk.co.nickthecoder.itchy.Itchy
import uk.co.nickthecoder.itchy.Role
import uk.co.nickthecoder.itchy.AbstractRole
import uk.co.nickthecoder.itchy.util.ClassName
import uk.co.nickthecoder.itchy.role.FollowerBuilder

class Player extends AbstractRole
{
    public static properties = new ArrayList()

            
    void onBirth()
    {
        // When the game starts tell "Play.groovy" we are alive and well, the game is on!
        Itchy.getGame().getSceneDirector().player = this;
    }
    
    void tick()
    {
        def game = Itchy.getGame()
        
        // Don't do anything when we are dead.
        if (game.getSceneDirector().isPlaying()) {
        
            // Get the X position of the mouse pointer, and then make sure that x isn't too far left or right.
            def x = Itchy.getMouseX()
            if ( x < 20 ) {
                x = 20
            }
            if ( x > game.getWidth() - 20 ) {
                x = game.getWidth() - 20
            }
            // Change the sheep's X, but leave the Y as it was.
            getActor().moveTo( x, getActor().getY() )

            // Have we hit anything deadly? (i.e. any actor that has : addTag("deadly") - see Drop.js).
            if ( getCollisionStrategy().collisions(getActor(),'deadly').size() > 0 ) {
            
                // Create a skeleton below the sheep, which will become visible when the sheep fades out.
                new FollowerBuilder(this.actor).followRotatation().pose('bones').adjustZOrder(-1).createActor();
                
                // Plays a sound, and starts an animation.
                getActor().deathEvent('death');
                
                game.getSceneDirector().end();
            }
        }
     }


    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        System.out.println( "Finding player's classname" );
        return new ClassName( Role, "Player.groovy" )
    }
    
}

