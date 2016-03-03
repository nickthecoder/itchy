import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class Player extends AbstractRole
{
    public static properties = new ArrayList()

    def outlineActor
    
    void onBirth()
    {
        // When the game starts tell "Play.groovy" we are alive and well, the game is on!
        Itchy.game.sceneDirector.player = this;
        outlineActor = new FollowerBuilder(actor).pose('outline').adjustZOrder(-1).create();
    }
    
    void tick()
    {
        def game = Itchy.game
        
        // Don't do anything when we are dead.
        if (game.sceneDirector.isPlaying()) {
        
            // Get the X position of the mouse pointer, and then make sure that x isn't too far left or right.
            def x = Itchy.mouseX
            if ( x < 20 ) {
                x = 20
            }
            if ( x > game.width - 20 ) {
                x = game.width - 20
            }
            // Change the sheep's X, but leave the Y as it was.
            actor.moveTo( x, actor.y )

            // Have we hit anything deadly? (i.e. any actor that has : addTag("deadly") - see Drop.js).
            if ( collided('deadly') ) {
                outlineActor.deathEvent("die")
                
                // Create a skeleton below the sheep, which will become visible when the sheep fades out.
                new FollowerBuilder(actor).followRotatation().pose('bones').adjustZOrder(-1).create();
                
                // Plays a sound, and starts an animation.
                actor.deathEvent('death');
                
                game.sceneDirector.end();
            }
        }
        outlineActor.tick();
     }


    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
		return new ClassName( Role, "Player.groovy" )
    }
}
