stdout.println( "Loading Moving.js");
Moving = Class({
    Extends: RoleScript,
    
    init: function() {
    	this.vx = 0;
    	this.vy = 0;
    },

    tick: function() {
        this.actor.moveBy(this.vx, this.vy);
    	
        if (this.actor.getX() < -10) this.actor.moveBy(820,0);
        if (this.actor.getX() > 810) this.actor.moveBy(-820,0);
        if (this.actor.getY() < -10) this.actor.moveBy(0,620);
        if (this.actor.getY() > 610) this.actor.moveBy(0,-620);
	
    }
});
