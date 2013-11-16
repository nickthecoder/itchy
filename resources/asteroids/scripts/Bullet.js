Bullet = Class({
    Extends: RoleScript,
    
    init: function() {
        this.speed = 1;
    },

    tick: function() {
    	// The bullet's animation will take care of moving forwards.    	
    	if (this.actor.getX() < -10) this.actor.moveBy(820,0);
        if (this.actor.getX() > 810) this.actor.moveBy(-820,0);
        if (this.actor.getY() < -10) this.actor.moveBy(0,620);
        if (this.actor.getY() > 610) this.actor.moveBy(0,-620);
        
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

