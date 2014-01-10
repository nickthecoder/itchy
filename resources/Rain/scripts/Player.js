Player = new Class({

    Extends: RoleScript,
    
    onBirth: function() {
        // When the game starts tell "Play.js" we are alive and well, the game is on!
        sceneDirectorScript.player = this;
        // BTW, sceneDirectorScript.player is set to null, when the game ends.
    },
    
    tick: function() {
    
        // Don't do anything when we are dead.
        if (sceneDirectorScript.isPlaying()) {
        
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
            if ( this.role.getCollisionStrategy().collisions(this.actor,["deadly"]).size() > 0 ) {
            
                // Create a skeleton below the sheep, which will become visible when the sheep fades out.
                new itchy.role.Follower(this.actor).followRotatation().pose("bones").adjustZOrder(-1).createActor();
                
                // Plays a sound, and starts an animation.
                this.actor.deathEvent("death");
                
                sceneDirectorScript.end();
            }
        }
    }
});


