import("Moving.js")

Bullet = Class({
    Extends: Moving,
    
    init: function() {
    	Super();
        this.speed = 1;
    },

    tick: function() {
    	Super();
        
        var i = this.actor.pixelOverlap("shootable").iterator();
        // Shoot ONE of the rocks that the bullet is touching.
        if (i.hasNext()) {
        	var role = i.next().roleScript;
        	if (!role.actor.isDying()) {
        		role.shot(this);
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

