Play = new Class({

    Extends: SceneBehaviourScript,
    
    init: function()
    {
        this.player = null;
        this.speed = 5;
        this.speedUpAfter = 10;
        this.score = 0;
        this.maxSpeed = 15;
    },

    onActivate: function()
    {
        this.highScore = game.getPreferences().getInt( game.getSceneName() + ".highScore", 0);
        this.speedUp = itchy.extras.Timer.createTimerSeconds( this.speedUpAfter );
    },
    
    tick: function()
    {
        if (this.isPlaying()) {
            this.score += 1;
            if (this.score == this.highScore) {
                this.player.actor.event("highScore");            
            }       
        }
        
        if (this.speedUp.isFinished()) {
            this.speed += 1;
            if (this.speed > this.maxSpeed ) {
                this.speed = this.maxSpeed;
            }
            stdout.println("Increase Speed to " + this.speed );
            this.speedUp.reset();
        }
    },
    
    onKeyDown: function(ke)
    {
        if ((ke.symbol == ke.RETURN) && (!this.isPlaying())) {
            game.startScene( game.getSceneName() );
        }
        
        if (ke.symbol == ke.ESCAPE) {
            game.startScene("start");
            return true;
        }
        return false;
    },
    
    isPlaying: function()
    {
        return this.player != null;
    },
    
    end: function()
    {
        this.speed = 0;
        this.player = null;
        if (this.score > this.highScore) {
            game.getPreferences().putInt( game.getSceneName() + ".highScore", this.score );
            stdout.println("New value : " + game.getPreferences().getInt( game.getSceneName() + ".highScore", -1));
        }       

    }
});
SceneBehaviourScript.addProperty("Play", "speed", Integer, "Speed");
SceneBehaviourScript.addProperty("Play", "maxSpeed", Integer, "Maximum Speed");
SceneBehaviourScript.addProperty("Play", "speedUpAfter", Integer, "Speed Up After");

