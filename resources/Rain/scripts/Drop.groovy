import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

class Drop extends AbstractRole
{
    public static properties = new ArrayList()

    static {
        properties.add( new IntegerProperty("distance") )
    }

    // Height to add when it hits the bottom of the screen.
    def distance = 600
    
    def speed = 10
    
    def speedFactor = 1

    def random = new Random()

    void onBirth()
    {
        // Player checks to see if it has collided with any "deadly" objects.
        addTag('deadly')
        
        // Each type of drop can got down the screen at different speed. The speedFactor is set from
        // the editor's "Costumes" page, in the "Properties" section.
        speedFactor = costumeFeatures.speedFactor
        speed = Itchy.game.sceneDirector.speed * speedFactor
    }

    void tick()
    { 
        // The rain drop falls
        actor.moveBy( 0, -speed )
        
        // Hit the bottom of the screen?
        if (actor.y < 0) {

            // Create a splash - lots of tiny water droplets (which aren't deadly)
            new ExplosionBuilder(actor)
                .projectiles(5).gravity(-0.2)
                .fade(0.9, 3.5).speed(-1.5, 1.5).vy(5,8)
                .pose('droplet').create();
            
            // Blue drops make a "drop" sound, gold drops say a random phrase.
            if (Itchy.game.sceneDirector.isPlaying()) {
                actor.event('drip')
            }
            // Move above the top of the screen, with a random X value, and the latest speed.
            def x = random.nextInt(Itchy.game.width)
            actor.moveTo( x, actor.y + distance )
            speed = Itchy.game.sceneDirector.speed * speedFactor
        }
    }

    public CostumeFeatures createCostumeFeatures(Costume costume)
    {
        return new DropFeatures()
    }

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }

    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Role, "Drop.groovy" )
    }
}

public class DropFeatures implements CostumeFeatures
{
    protected static properties = new ArrayList()
    
    static {
        properties.add( new DoubleProperty( 'speedFactor' ) )
    }
    
    def speedFactor = 1

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }

}

