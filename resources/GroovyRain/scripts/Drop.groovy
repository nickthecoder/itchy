import uk.co.nickthecoder.itchy.Itchy
import uk.co.nickthecoder.itchy.Role
import uk.co.nickthecoder.itchy.AbstractRole
import uk.co.nickthecoder.itchy.util.ClassName

import uk.co.nickthecoder.itchy.role.ExplosionBuilder

import uk.co.nickthecoder.itchy.property.IntegerProperty

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
        // The speed of each drop is determined by the sceneDirector. See Play.js for more.
        // speed = Itchy.getGame().getSceneDirector().speed;
        
        // Each type of drop can got down the screen at different speed. The speedFactor is set from
        // the editor's "Costumes" page, in the "Properties" section.
        speedFactor = getActor().getCostume().getProperties().speedFactor
        speed = speed * speedFactor
    }

    void tick()
    { 
        // The rain drop falls
        getActor().moveBy( 0, -speed )
        
        // Hit the bottom of the screen?
        if ( getActor().getY() < 0) {

            // Create a splash - lots of tiny water droplets (which aren't deadly)
            new ExplosionBuilder(getActor())
                .projectiles(5).gravity(-0.2)
                .fade(0.9, 3.5).speed(-1.5, 1.5).vy(5,8)
                .pose('droplet').createActor();
            
            // Blue drops make a "drop" sound, gold drops say a random phrase.
            if (Itchy.getGame().getSceneDirector().isPlaying()) {
                getActor().event('drip')
            }
            // Move above the top of the screen, with a random X value, and the latest speed.
            def x = random.nextInt(Itchy.getGame().getWidth())
            getActor().moveTo( x, getActor().getY() + this.distance )
            speed = Itchy.getGame().getSceneDirector().speed * speedFactor
        }
    }


    // TODO Other methods include :
    // onSceneCreated, onDetach, onKill, onMouseDown, onMouseUp, onMouseMove



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

