import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.util.*

public class Sprites extends PlainSceneDirector
{
    public static properties = new ArrayList()

	static {
		// TODO declare properties here. Note that you must also add a "def" for each one.
        // properties.add( new StringProperty( "myString" ) )
    }

    def random = new Random()
    
    def angryCostume = Itchy.game.resources.getCostume( "angry" )
    def count = 0;
    def time = System.currentTimeMillis()
    def frameCounter = 0
    
    // Constructor
    public Sprites()
    {
    }
        
    public void tick()
    {
        frameCounter ++
        if (frameCounter > 60) {
            frameCounter = 0
            def fps = 60000 / (System.currentTimeMillis() - time)
            time = System.currentTimeMillis()
            println( "Sprite count " + count )
            println( "FPS = " + fps )
        }
    
        if ( Itchy.game.frameRate.droppedFrames < 10 ) {
            count ++
            def Actor actor = angryCostume.createActor()
            actor.moveTo( random.nextInt(800), random.nextInt(600) )
            Itchy.game.layout.findLayer("main").stage.add(actor)
        }
    }

    // TODO Other methods include :
    // onActivate, onDeactivate(), onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
    
    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( CostumeProperties, "Sprites.groovy" )
    }
}
