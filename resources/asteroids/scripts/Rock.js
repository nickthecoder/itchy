import("Moving.js")

Rock = Class({
    Extends: Moving,
    
    init: function() {
    	Super();
        this.rotationSpeed = 0;
        this.hits = 0;
    },
    
    onBirth: function() {
        this.role.addTag("shootable");
        this.role.addTag("deadly");
        sceneDirectorScript.addRocks(1);
    },
    
    tick: function() {
    	Super();
    	
        this.actor.getAppearance().adjustDirection(this.rotationSpeed);
    },
    
    shot: function(bullet) {
    	// Small bullets have NO effect on strong rocks.
    	if (bullet.getCostumeProperties().strength < this.getCostumeProperties().strength) {
    		this.actor.event("ricochet");
    		return;
    	}
    	
    	this.hits += bullet.getCostumeProperties().strength;
    	// Have we hit the rock enough times?
    	if ( this.hits < this.getCostumeProperties().hitsRequired) {
    		this.actor.event("hit");
    		return;
    	}
    	
		this.actor.event("explode");
    	directorScript.addPoints(this.getCostumeProperties().points);
    	
        new itchy.extras.Explosion(this.actor)
            .spread( bullet.actor.getHeading() - 120, bullet.actor.getHeading() + 120 ).randomSpread()
            .speed(5,3,0,0).fade(3).distance(40)
            .rotate(true).pose("fragment").projectiles(8	)
            .createActor();

        var pieces = this.getCostumeProperties().pieces;
        for (var i = 0; i < pieces; i ++ ) {
            var actor = this.actor.createCompanion("fragment");
            var role = actor.getRole().roleScript;
            
            role.rotationSpeed = 3 * i - this.rotationSpeed;
            role.vx = itchy.util.Util.randomBetween( -4, 4 );
            role.vy = itchy.util.Util.randomBetween( -4, 4 );
            
            actor.setDirection( this.actor.getHeading() + i * 120 );
            actor.moveForwards( 40 );
        }
        sceneDirectorScript.addRocks(-1);
        this.actor.deathEvent("explode");
        this.role.removeTag("shootable");
    }
});
RoleScript.addProperty("Rock", "rotationSpeed", Double, "Rotation Speed (Degrees per Tick)");
RoleScript.addProperty("Rock", "vx", Double, "X Velocity");
RoleScript.addProperty("Rock", "vy", Double, "Y Velocity");

