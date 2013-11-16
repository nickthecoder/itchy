// The SceneDirectorScript while playing the game - there is a different SceneDirectorScript for the menus (Menu.js)
Play = new Class({

    Extends: SceneDirectorScript,
    
    init: function()
    {
        this.player = null; // The sheep's behaviour. Set from Player.js
        this.score = 0;
        
        // Each level (easy,medium,hard) has different values for each of the following.
        // Change them from the Scene Designer's "Scene" section.
        this.speed = 5; // The default initial speed. 
        this.speedUpAfter = 10; // The time in seconds for each increase in speed.
        this.maxSpeed = 15; // If you reach this speed, then it gets no harder.
    },

    onActivate: function()
    {
        // High scores are saved. There is a high score for each level. If there is no high score, then use 0.
        this.highScore = game.getPreferences().getInt( game.getSceneName() + ".highScore", 0);
        
        // Create a timer, which will tell us when to increase the speed.
        this.speedUp = itchy.extras.Timer.createTimerSeconds( this.speedUpAfter );
    },
    
    tick: function()
    {
        if (this.isPlaying()) {
            this.score += 1;
            
            if (this.score == this.highScore) {
                // Sound "Woo"
                this.player.actor.event("highScore");            
            }
            
            // Is it time for a speed increase yet?
            if (this.speedUp.isFinished()) {
                this.speed += 1;
                // Don't exceed the speed limit!
                if (this.speed > this.maxSpeed ) {
                    this.speed = this.maxSpeed;
                }
                stdout.println("Increase Speed to " + this.speed );
                // Reset the timer, so that it can tell us when the NEXT speed up is due.
                this.speedUp.reset();
            }
        }
    },
    
    onKeyDown: function(ke)
    {
        // If we are dead, then the RETURN key will start a new game (reloads the current scene)
        if ((ke.symbol == ke.RETURN) && (!this.isPlaying())) {
            game.startScene( game.getSceneName() );
            return true; // Return true to indicate that the key has been processed.
        }
        
        // Escape key takes us back to the menu.
        if (ke.symbol == ke.ESCAPE) {
            game.startScene("menu");
            return true; // Return true to indicate that the key has been processed.
        }
        return false;
    },
    
    isPlaying: function()
    {
        return this.player != null;
    },
    
    end: function()
    {
        // This is how "isPlaying" knows that the game has ended.
        this.player = null;
        
        // If we've beaten the high score, store in into preferences. Preferences are stored permanently,
        // so the high score will still be there when we play the game weeks or months later.
        if (this.score > this.highScore) {
            game.getPreferences().putInt( game.getSceneName() + ".highScore", this.score );
        }       

    }
});
// Define the scene's properties. These properties can then be editted within the Scene Designer,
// within the "Scene" section. Each scene has its own values,
// i.e. scene "easy" will have a different "speed" to scene "hard".
SceneDirectorScript.addProperty("Play", "speed", Integer, "Speed"); // The initial speed
SceneDirectorScript.addProperty("Play", "maxSpeed", Integer, "Maximum Speed"); // The maximum speed
SceneDirectorScript.addProperty("Play", "speedUpAfter", Integer, "Speed Up After"); // The time in seconds till we increase the speed.

