Drop = Class({
    Extends: BehaviourScript,

    init: function() {
        // This is the default height that the drop will return to, after it hits the floor.
        // Each Drop can have it own height set within the Scene Designer - the "Behaviours" section.
        this.distance = 600;
    },

    onBirth: function() {
        // Player checks to see if it has collided with any "deadly" objects.
        this.behaviour.addTag("deadly");
        // The speed of each drop is determined by the sceneBehaviour. See Play.js for more.
        this.speed = sceneBehaviourScript.speed;
        // Each type of drop can got down the screen at different speed. The speedFactor is set from
        // the editor's "Costumes" page, in the "Properties" section.
        this.speedFactor = this.actor.getCostume().getProperties().values.speedFactor;
        this.speed *= this.speedFactor;
    },

    tick: function() { 
        // The rain drop falls
        this.actor.moveBy( 0, - this.speed );
        
        // Hit the bottom of the screen?
        if ( this.actor.getY() < 0) {

            // Create a splash - lots of tiny water droplets (which aren't deadly)
            new itchy.extras.Explosion(this.actor)
                .projectiles(5).gravity(-0.2)
                .fade(0.9, 3.5).speed(-1.5, 1.5).vy(5,8)
                .pose("droplet").createActor();
            
            // Blue drops make a "drop" sound, gold drops say a random phrase.
            if (sceneBehaviourScript.isPlaying()) {
                this.actor.event("drip");
            }
            // Move above the top of the screen, with a random X value, and the latest speed.
            var x = random.nextInt(Itchy.getGame().getWidth());
            this.actor.moveTo( x, this.actor.getY() + this.distance );
            this.speed = sceneBehaviourScript.speed * this.speedFactor;
            
        }
    }
});
// Define the properties of a Drop.
// The properties can be editted in the Scene Designer within the "Behaviour" section.
// Every single drop can have its own "distance"

// The height to restart from after hitting the ground.
// If every drop has the same "distance", then the Y spacing will remain the same for the whole game.
// But if some restart higher than others, then the Y spacing will vary. Sometimes, lots of drops will have
// a similar "Y" value, which makes finding a safe gap for the sheep difficult.
BehaviourScript.addProperty("Drop", "distance", Integer, "Distance", 600); 

