Player = new Class( Behaviour )
({
    onActivate: function() {
        sceneBehaviour.player = this;
    },
    
    tick: function() {
        if (sceneBehaviour.isPlaying()) {
            var x = Itchy.getMouseX();
            if ( x < 20 ) {
                x = 20;
            }
            if ( x > game.getWidth() - 20 ) {
                x = game.getWidth() - 20;
            }
            this.actor.moveTo( x, this.actor.getY() );
            
            if ( this.owner.touching("deadly").size() > 0 ) {
                this.actor.deathEvent("death");
                sceneBehaviour.end();
            }
        }
    }
});


