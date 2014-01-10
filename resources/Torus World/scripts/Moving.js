stdout.println( "Loading Moving.js");
Moving = Class({
    Extends: RoleScript,
    
    init: function() {
    	this.vx = 0;
    	this.vy = 0;
    },

    tick: function() {
        this.actor.moveBy(this.vx, this.vy);
    }
});
