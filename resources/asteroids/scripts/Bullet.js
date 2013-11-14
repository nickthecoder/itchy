Bullet = Class({
    Extends: BehaviourScript,
    
    init: function() {
        this.speed = 1;
    },
    
    tick: function() {
        this.actor.moveForwards(this.speed);
        
        if (this.actor.getX() < -10) this.actor.moveBy(820,0);
        if (this.actor.getX() > 810) this.actor.moveBy(-820,0);
        if (this.actor.getY() < -10) this.actor.moveBy(0,620);
        if (this.actor.getY() > 610) this.actor.moveBy(0,-620);
        
        var i = this.actor.pixelOverlap("shootable").iterator();
        // Shoot ONE of the rocks that the bullet is touching.
        if (i.hasNext()) {
        	var behaviour = i.next().behaviourScript;
        	if (!behaviour.actor.isDying()) {
        		stdout.println("Shot " + behaviour + " " + behaviour.actor);
        		behaviour.shot(this);
        		this.actor.kill();
        	}
        }
    },

    onMessage: function(message) {
    	if (message=="die") {
    		// Sent at the end of the fade out animation. Its the animation that determines the bullets max life span.
    		this.actor.kill();
    	}
    }
});

