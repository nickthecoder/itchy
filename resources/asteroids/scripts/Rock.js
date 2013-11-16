Rock = Class({
    Extends: BehaviourScript,
    
    init: function() {
        this.rotationSpeed = 0;
        this.vx = 0;
        this.vy = 0;
        this.hits = 0;
    },
    
    onBirth: function() {
        this.behaviour.addTag("shootable");
        this.behaviour.addTag("deadly");
        sceneBehaviourScript.addRocks(1);
    },
    
    tick: function() {
        this.actor.moveBy(this.vx, this.vy);
        this.actor.getAppearance().adjustDirection(this.rotationSpeed);
        
        if (this.actor.getX() < -10) this.actor.moveBy(820,0);
        if (this.actor.getX() > 810) this.actor.moveBy(-820,0);
        if (this.actor.getY() < -10) this.actor.moveBy(0,620);
        if (this.actor.getY() > 610) this.actor.moveBy(0,-620);
    },
    
    shot: function(bullet) {
    	// Small bullets have NO effect on strong rocks.
    	if (bullet.getCostumeProperties().strength < this.getCostumeProperties().strength) {
    		return;
    	}
    	
    	this.hits += bullet.getCostumeProperties().strength;
    	// Have we hit the rock enough times?
    	if ( this.hits < this.getCostumeProperties().hitsRequired) {
    		return;
    	}
    	
    	gameScript.addPoints(this.getCostumeProperties().points);
    	
        new itchy.extras.Explosion(this.actor)
            .spread( bullet.actor.getHeading() - 120, bullet.actor.getHeading() + 120 ).randomSpread()
            .speed(5,3,0,0).fade(3).distance(40)
            .rotate(true).pose("fragment").projectiles(8	)
            .createActor();

        var pieces = this.getCostumeProperties().pieces;
        for (var i = 0; i < pieces; i ++ ) {
            var actor = this.actor.createCompanion("fragment");
            var behaviour = actor.getBehaviour().behaviourScript;
            
            behaviour.rotationSpeed = 3 * i - this.rotationSpeed;
            behaviour.vx = itchy.util.Util.randomBetween( -4, 4 );
            behaviour.vy = itchy.util.Util.randomBetween( -4, 4 );
            
            actor.setDirection( this.actor.getHeading() + i * 120 );
            actor.moveForwards( 40 );
        }
        sceneBehaviourScript.addRocks(-1);
        this.actor.deathEvent("explode");
        this.behaviour.removeTag("shootable");
    }
});
BehaviourScript.addProperty("Rock", "rotationSpeed", Double, "Rotation Speed (Degrees per Tick)");
BehaviourScript.addProperty("Rock", "vx", Double, "X Velocity");
BehaviourScript.addProperty("Rock", "vy", Double, "Y Velocity");

