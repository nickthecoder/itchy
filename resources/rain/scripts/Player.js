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
            
            if ( this.behaviour.touching("deadly").size() > 0 ) {
                this.actor.deathEvent("death");
                sceneBehaviourScript.end();
            }
        }
    }
});


