Drop = Class({
    Extends: BehaviourScript,
    
    init: function() {
        this.distance = 600;
        this.speed = sceneBehaviourScript.speed;
    },

    onAttach: function() {
        this.actor.addTag("deadly");
        this.speedFactor = this.actor.getCostume().getProperties().values.speedFactor;
        this.speed *= this.speedFactor;
    },

    tick: function() {
        this.actor.moveBy( 0, - this.speed );
        if ( this.actor.getY() < 0) {

            new itchy.extras.Explosion(this.actor)
                .projectiles(10).gravity(-0.2)
                .forwards().fade(0.9, 3.5).speed(0.1, 1.5).vy(5)
                .createActor("droplet").activate();
                    
            if (sceneBehaviourScript.isPlaying()) {
                this.actor.event("drip");
                var x = random.nextInt(Itchy.getGame().getWidth());
                this.actor.moveTo( x, this.actor.getY() + this.distance );
                this.speed = sceneBehaviourScript.speed * this.speedFactor;
            }
            
        }
    }
});
BehaviourScript.addProperty("Drop", "distance", Integer, "Distance", 600);

