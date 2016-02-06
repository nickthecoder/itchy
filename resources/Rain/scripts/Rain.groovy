import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.util.*

public class Rain extends AbstractDirector
{    
    double customRainDrops
    double customFastDrops
    double customFrogs
    double customSpeed
    double customMaxSpeed
    double customSpeedUpAfter

    public void onActivate()
    {
        getCustomValues()
    }

    public void getCustomValues()
    {
        def preferences = Itchy.game.preferences.node( "custom" )

        this.customRainDrops = preferences.getInt( "rainDrops", 4 )
        this.customFastDrops = preferences.getInt( "fastDrops", 1 )
        this.customFrogs = preferences.getInt( "frogs", 0 )

        this.customSpeed = preferences.getInt( "speed", 6 )
        this.customMaxSpeed = preferences.getInt( "maxSpeed", 11 )
        this.customSpeedUpAfter = preferences.getInt( "speedUpAfter", 10 )
    }
    
    public void putCustomValues()
    {
        def preferences = Itchy.game.preferences.node( "custom" )
        
        preferences.putInt( "rainDrops", (int) this.customRainDrops )
        preferences.putInt( "fastDrops", (int) this.customFastDrops )
        preferences.putInt( "frogs", (int) this.customFrogs )

        preferences.putInt( "speed", (int) this.customSpeed )
        preferences.putInt( "maxSpeed", (int) this.customMaxSpeed )
        preferences.putInt( "speedUpAfter", (int) this.customSpeedUpAfter )
    }

    // Boiler plate code - no need to change this
    public ClassName getClassName()
    {
        return new ClassName( Director, "Rain.groovy" )
    }
}
