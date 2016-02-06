import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.util.*
import Play

public class Custom extends Play
{

    public void onLoaded()
    {
        super.onLoaded()
        def director = Itchy.game.director

        this.speed = director.customSpeed
        this.maxSpeed = director.customMaxSpeed
        this.speedUpAfter = director.customSpeedUpAfter
        
        createRain( (int) director.customRainDrops, "drop1" )
        createRain( (int) director.customFrogs, "drop2" )
        createRain( (int) director.customFastDrops, "drop3" )

        director.putCustomValues()
    }

    String getHighScoreKey()
    {
        def director = Itchy.game.director
        
        return "custom_" +
            (int) director.customRainDrops + "_" +
            (int) director.customFrogs + "_" +
            (int) director.customFastDrops + "_" +
            (int) director.customSpeed + "_" +
            (int) director.customMaxSpeed + "_" +
            (int) director.customSpeedUpAfter + "_" +
            ".highScore"
    }

    private  void createRain( int amount, String costumeName )
    {
        def game = Itchy.game
        def resources = game.resources
        def random = new Random()
        
        for ( int i = 0; i < amount; i ++ ) {
            // Create the rain drop
            def actor = resources.getCostume( costumeName ).createActor()
            actor.role.distance = 600 + random.nextInt(3) * 5
            actor.x = random.nextInt( game.width )
            actor.y = game.height + random.nextInt( game.height )
            Itchy.game.layout.findStage("main").add(actor)
        }
    }

    // TODO Other methods include :
    // onActivate, onDeactivate(), onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
    
    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( CostumeProperties, "Custom.groovy" )
    }
}
