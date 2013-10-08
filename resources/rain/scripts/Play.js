Play = new Class({

    Extends: SceneBehaviourScript,
    
    init: function() {
        this.player = null;
        this.speed = 5;
        this.score = 0;
        this.highScore = game.getPreferences().getInt( game.getSceneName() + ".highScore", 0);
        this.levelUp = itchy.extras.Timer.createTimerSeconds( 10 );
    },

    tick: function() {
    
        if (this.isPlaying()) {
            this.score += 1;
            if (this.score == this.highScore) {
                this.player.actor.event("highScore");            
            }       
        }
        
        if (this.levelUp.isFinished()) {
            this.speed += 1;
            if (this.speed > 15 ) {
                this.speed = 15;
            }
            stdout.println("Increase Speed to " + this.speed );
            this.levelUp.reset();
        }
    },
    
    onKeyDown: function(ke) {
        if (ke.symbol == ke.ESCAPE) {
            game.startScene("start");
            return true;
        }
        return false;
    },
    
    isPlaying: function() {
        return this.player != null;
    },
    
    end: function() {
        this.speed = 0;
        this.player = null;
        if (this.score > this.highScore) {
            game.getPreferences().putInt( game.getSceneName() + ".highScore", this.score );
            stdout.println("New value : " + game.getPreferences().getInt( game.getSceneName() + ".highScore", -1));
        }       

    }
});

