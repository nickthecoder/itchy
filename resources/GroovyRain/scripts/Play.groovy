import uk.co.nickthecoder.jame.event.KeyboardEvent
import uk.co.nickthecoder.itchy.Itchy
import uk.co.nickthecoder.itchy.Input
import uk.co.nickthecoder.itchy.PlainSceneDirector
import uk.co.nickthecoder.itchy.util.ClassName
import uk.co.nickthecoder.itchy.extras.Timer
import uk.co.nickthecoder.itchy.property.IntegerProperty

public class Play extends PlainSceneDirector
{
    protected static properties = new ArrayList()
    
    static {
        // Define the scene's properties. These properties can then be editted within the Scene Designer,
        // within the "Scene" section. Each scene has its own values,
        // i.e. scene "easy" will have a different "speed" to scene "hard".
        properties.add( new IntegerProperty("speed") );
        properties.add( new IntegerProperty("maxSpeed").label("Maximum Speed") );
        properties.add( new IntegerProperty("speedUpAfter") );
    }
    
    
    def speed = 5
    
    def speedUpAfter = 10
    
    def maxSpeed = 15
    
    def player = null
    
    def score = 0
    
    def highScore = 0
    
    def speedUpTimer

    def inputRestart
    
    def inputExit

    void onActivate()
    {
        inputRestart = Input.find('restart')
        inputExit = Input.find('exit')

        // High scores are saved. There is a high score for each level. If there is no high score, then use 0.
        highScore = Itchy.getGame().getPreferences().getInt( Itchy.getGame().getSceneName() + ".highScore", 0)
        
        // Create a timer, which will tell us when to increase the speed.
        speedUpTimer = Timer.createTimerSeconds( speedUpAfter )
    }
    
    void tick()
    {
        if (isPlaying()) {
            score += 1
            
            if (score == highScore) {
                // Sound "Woo"
                player.actor.event("highScore")
            }
            
            // Is it time for a speed increase yet?
            if (speedUpTimer.isFinished()) {
                speed += 1
                // Don't exceed the speed limit!
                if (speed > maxSpeed ) {
                    speed = maxSpeed
                }
                // Reset the timer, so that it can tell us when the NEXT speed up is due.
                speedUpTimer.reset()
            }
        }
    }
    
    boolean onKeyDown( KeyboardEvent ke )
    {
        
        // If we are dead, then the RETURN key will start a new game (reloads the current scene)
        if ((!isPlaying()) && inputRestart.matches(ke)) {
            Itchy.getGame().startScene( Itchy.getGame().getSceneName() )
            return true // Return true to indicate that the key has been processed.
        }

        // Escape key takes us back to the menu.
        if (inputExit.matches(ke)) {
            Itchy.getGame().startScene("menu")
            return true // Return true to indicate that the key has been processed.
        }
        return false
    }

    def isPlaying()
    {
        return player != null
    }
    
    void end()
    {
        // This is how "isPlaying" knows that the game has ended.
        player = null
        
        // If we've beaten the high score, store in into preferences. Preferences are stored permanently,
        // so the high score will still be there when we play the game weeks or months later.
        if (score > highScore) {
            Itchy.getGame().getPreferences().putInt( Itchy.getGame().getSceneName() + ".highScore", score )
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
        return new ClassName( CostumeProperties, "Play.groovy" )
    }

}


