Ship = Class({
    Extends: RoleScript,
    
    init: function() {
        this.vx=0;
        this.vy=0;
        this.lifeIcon = new Array();
        this.killed = false;
    },

    onBirth: function() {

    	this.rotationSpeed = this.getCostumeProperties().rotationSpeed;
    	this.thrust = this.getCostumeProperties().thrust;
    	this.firePeriod = this.getCostumeProperties().firePeriod;
    	
        this.fireTimer = new itchy.extras.Timer.createTimerSeconds(this.firePeriod);        
        new itchy.extras.Fragment().actor(this.actor).pieces(10).createPoses("fragment");
        sceneDirectorScript.ship = this;
		
		new itchy.extras.Explosion(this.actor)
			.distance(-30,-100).direction(0,360).speed(0,0).eventName("warp").projectiles(40)
			.createActor();

		new itchy.extras.Explosion(this.actor)
			.distance(-60).direction(0,360).speed(4,2).pose("explosion").fade(3).projectiles(20).randomSpread(false)
			.createActor();
	
		for (var i = 0; i < directorScript.lives; i ++ ) {
			var actor = this.actor.createCompanion("life");
			actor.moveTo( 30 + i * 40 , 560 );
			if (game.getSceneName() == "1") {
				actor.event("appear");
			}
			this.lifeIcon[i] = actor;
		}
	
    },

    warp: function() {
    	this.vx = 0;
    	this.vy = 0;
    	for (var i = 0; i < 3; i ++) {
    		new itchy.extras.Explosion(this.actor)
    			.spread(i*120, 360 + i*120).vx(this.vx).vy(this.vy).distance(300)
    			.speed(-6,0).pose("warp").projectiles(20).projectilesPerTick(1).randomSpread(false).alpha(0).fade(-3)
    			.createActor();
    	}
    	this.actor.deathEvent("fade");
    },
    
    tick: function() {

        if (Itchy.isKeyDown(Keys.LEFT)) {
            this.actor.adjustDirection( this.rotationSpeed );
        }
        if (Itchy.isKeyDown(Keys.RIGHT)) {
            this.actor.adjustDirection( -this.rotationSpeed );
        }
        if (Itchy.isKeyDown(Keys.UP)) {
            var theta = this.actor.getHeadingRadians();
            this.vx += Math.cos(theta) * this.thrust;
            this.vy += Math.sin(theta) * this.thrust;
        }
        if (Itchy.isKeyDown(Keys.SPACE)) {
            if (this.fireTimer.isFinished()) {
                this.fire();
                this.fireTimer.reset();
            }
        }
        this.actor.moveBy(this.vx, this.vy);

        if (this.actor.getX() < -10) this.actor.moveBy(820,0);
        if (this.actor.getX() > 810) this.actor.moveBy(-820,0);
        if (this.actor.getY() < -10) this.actor.moveBy(0,620);
        if (this.actor.getY() > 610) this.actor.moveBy(0,-620);

        
        if ( ! this.actor.pixelOverlap("deadly").isEmpty() ) {
            this.die();
        }
        var i = this.actor.pixelOverlap("shootable").iterator();
        while (i.hasNext()) {
            i.next().roleScript.shot(this);
        }
        
        // For debugging.
        if (Itchy.isKeyDown(Keys.x)) {
        	sceneDirectorScript.addRocks(-1);
        }
        
    },
    
    die: function() {
   	
        new itchy.extras.Explosion(this.actor)
            .speed(3,1).fade(3).spin(-5,5).rotate(true).pose("fragment").projectiles(40).createActor();
        
        directorScript.lives -= 1;
        this.killed = true;

        this.lifeIcon[directorScript.lives].event("disappear");
        this.actor.deathEvent("explode");
    },
    
    onDeath: function() {
    	if (this.killed) {
    		if (directorScript.lives > 0) {
    			game.startScene(game.getSceneName());
	    	} else {
	    		for ( var i = itchy.AbstractRole.allByTag( "gameOver" ).iterator(); i.hasNext();) {
	    			var gameOver = i.next();
	    			gameOver.event("reveal");
	    		}
	    	}
    	}
    },
    
    fire: function() {
        var actor = this.actor.createCompanion("bullet");

        actor.setDirection( this.actor.getHeading() );
        actor.moveTo( this.actor );
        actor.moveForwards(40);

        var impulse = actor.getCostume().getProperties().values.impulse;
        var theta = this.actor.getHeadingRadians();
        this.vx -= Math.cos(theta) * impulse;
        this.vy -= Math.sin(theta) * impulse;
    }
    
    
});

