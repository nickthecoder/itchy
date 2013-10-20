Player = new Class({

    Extends: BehaviourScript,
    
    onActivate: function() {
        // When the game starts tell "Play.js" we are alive and well, the game is on!
        sceneBehaviourScript.player = this;
        // BTW, sceneBehaviourScript.player is set to null, when the game ends.
    },
    
    tick: function() {
    
        // Don't do anything when we are dead.
        if (sceneBehaviourScript.isPlaying()) {
        
            // Get the X position of the mouse pointer, and then make sure that x isn't too far left or right.
            var x = Itchy.getMouseX();
            if ( x < 20 ) {
                x = 20;
            }
            if ( x > game.getWidth() - 20 ) {
                x = game.getWidth() - 20;
            }
            // Change the sheep's X, but leave the Y as it was.
            this.actor.moveTo( x, this.actor.getY() );
            
            // Have we hit anything deadly? (i.e. any actor that has : addTag("deadly") - see Drop.js).
            if ( this.behaviour.pixelOverlap("deadly").size() > 0 ) {
            
                // Create a skeleton below the sheep, which will become visible when the sheep fades out.
                new itchy.extras.Follower(this.actor).rotate().createActor("bones", true).activate();
                
                // Plays a sound, and starts an animation.
                this.actor.deathEvent("death");
                
                sceneBehaviourScript.end();
            }
        }
    }
});


