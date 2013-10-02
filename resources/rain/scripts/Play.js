Play = new Class( SceneBehaviour )
({
    __init__: function() {
        this.player = null;
        this.speed = 5;
        this.score = 0;
        stdout.println("Scene : " + game.getSceneName() );
        this.highScore = game.getPreferences().getInt( game.getSceneName() + ".highScore", 0);
        stdout.println("Highscore : " + this.highScore );
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
        stdout.println( "Score " + this.score + " vs " + this.highScore + " ? " + (this.score > this.highScore) );
        if (this.score > this.highScore) {
            stdout.println("Setting Highscore : " + this.score );
            game.getPreferences().putInt( game.getSceneName() + ".highScore", this.score );
            stdout.println("New value : " + game.getPreferences().getInt( game.getSceneName() + ".highScore", -1));
        }       

    }
});

