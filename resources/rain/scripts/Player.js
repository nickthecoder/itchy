Player = new Class({

    Extends: BehaviourScript,
    
    onActivate: function() {
        sceneBehaviourScript.player = this;
    },
    
    tick: function() {
        if (sceneBehaviourScript.isPlaying()) {
            var x = Itchy.getMouseX();
            if ( x < 20 ) {
                x = 20;
            }
            if ( x > game.getWidth() - 20 ) {
                x = game.getWidth() - 20;
            }
            this.actor.moveTo( x, this.actor.getY() );
            
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


