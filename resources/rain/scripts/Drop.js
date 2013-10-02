Drop = new Class( Behaviour )
({   
    __init__: function() {
        this.distance = 600;
        this.speed = sceneBehaviour.speed;
    },

    onAttach: function() {
        this.actor.addTag("deadly");
    },
    
    tick: function() {
        this.actor.moveBy( 0, - this.speed );
        if ( this.actor.getY() < 0) {

            new itchy.extras.Explosion(this.actor)
                .projectiles(10).gravity(-0.2)
                .forwards().fade(0.9, 3.5).speed(0.1, 1.5).vy(5)
                .createActor("droplet").activate();
                    
            if (sceneBehaviour.isPlaying()) {
                this.actor.event("drip");
                var x = random.nextInt(Itchy.getGame().getWidth());
                this.actor.moveTo( x, this.actor.getY() + this.distance );
                this.speed = sceneBehaviour.speed;
            }
            
        }
    }
});
Behaviour.integerProperty("Drop","distance", "Distance", 600);

