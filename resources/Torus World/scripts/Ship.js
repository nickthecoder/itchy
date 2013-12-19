import("Moving.js")

Ship = Class({
    Extends: Moving,
    
    init: function() {
    	Super();
        this.lifeIcon = new Array();
    },

    onBirth: function() {

    	this.rotationSpeed = this.getCostumeProperties().rotationSpeed;
    	this.thrust = this.getCostumeProperties().thrust;
    	this.firePeriod = this.getCostumeProperties().firePeriod;
    	
        this.fireTimer = new itchy.extras.Timer.createTimerSeconds(this.firePeriod);        

        // Cut the ship into 3 large pieces, and call these poses "part"
        new itchy.extras.Fragment().actor(this.actor).pieces(3).createPoses("part");
        // Cut the ship again, this time into 10 pieces, and call these poses "fragment".
        new itchy.extras.Fragment().actor(this.actor).pieces(10).createPoses("fragment");
        // These are use together when the ship explodes in the "die" method.

        sceneDirectorScript.ship = this;
		
		new itchy.role.Explosion(this.actor)
			.companion("warp").eventName("default").distance(-30,-100).direction(0,360).speed(0,0).projectiles(40)
			.createActor();

		//new itchy.role.Explosion(this.actor)
		//	.companion("explosion")
		//	.distance(-60).direction(0,360).speed(4,2).fade(3).projectiles(20).randomSpread(false)
		//	.createActor();

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
    		new itchy.role.Explosion(this.actor)
    			.companion("warp")
    			.spread(i*120, 360 + i*120).vx(this.vx).vy(this.vy).distance(300)
    			.speed(-6,0).projectiles(20).projectilesPerTick(1).randomSpread(false).alpha(0).fade(-3)
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
            var heading = this.actor.getDirection();
        	new itchy.role.Explosion(this.actor)
    			.projectiles(4).follow().projectilesPerTick(1)
    			.spread(heading+160, heading+200).distance(40)
    			.randomSpread().speed(1,2,0,0).fade(3).eventName("spark")
    			.createActor();
        }
        if (Itchy.isKeyDown(Keys.SPACE)) {
            if (this.fireTimer.isFinished()) {
                this.fire();
                this.fireTimer.reset();
            }
        }
        // Move and wrap from one edge of the world to the opposite.
        Super();
        
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
   	
   	    // Use the "fragment" and "part" poses created in onBirth to explode the ship in all directions.
   	    // The large "part" pieces move slowly, and the smaller "fragment" pieces move quickly.
        new itchy.role.Explosion(this.actor)
            .speed(0.5,0,1,0).fade(3).spin(-1,1).rotate(true).eventName("part").projectiles(4).createActor();
        new itchy.role.Explosion(this.actor)
            .speed(1.5,0,4,0).fade(3).spin(-1,1).rotate(true).eventName("fragment").projectiles(20).createActor();
        
        directorScript.lives -= 1;

        this.lifeIcon[directorScript.lives].event("disappear");
        this.actor.deathEvent("explode", "exploded");
    },
    
    onMessage: function( message ) {
    	if (message == "exploded") {
			if (directorScript.lives > 0) {
				game.startScene(game.getSceneName());
	    	} else {
	    		directorScript.showFancyMouse();
	            game.loadScene("gameOver", true);	    		
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

