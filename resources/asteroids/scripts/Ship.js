Ship = Class({
    Extends: BehaviourScript,
    
    init: function() {
        this.rotationSpeed=10;
        this.thrust=0.6;
        this.bulletSpeed = 14;
        this.firePeriod=0.1;
        this.vx=0;
        this.vy=0;
    },

    onBirth: function() {
        this.fireTimer = new itchy.extras.Timer.createTimerSeconds(this.firePeriod);        
        new itchy.extras.Fragment().actor(this.actor).pieces(10).createPoses("fragment");
    },
    
    tick: function() {
        if (Itchy.isKeyDown(jame.event.Keys.LEFT)) {
            this.actor.setDirection(this.actor.getHeading() + this.rotationSpeed);
        }
        if (Itchy.isKeyDown(jame.event.Keys.RIGHT)) {
            this.actor.setDirection(this.actor.getHeading() - this.rotationSpeed);
        }
        if (Itchy.isKeyDown(jame.event.Keys.UP)) {
            var theta = this.actor.getHeadingRadians();
            this.vx += Math.cos(theta) * this.thrust;
            this.vy += Math.sin(theta) * this.thrust;
        }
        if (Itchy.isKeyDown(jame.event.Keys.SPACE)) {
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
            i.next().getBehaviour().behaviourScript.shot(this);
            this.actor.kill();
        }
    },
    
    die: function() {
        this.actor.deathEvent("explode");
        new itchy.extras.Explosion(this.actor)
            .speed(3,1).fade(3).spin(-5,5).rotate(true).pose("fragment").projectiles(40)
            .createActor();
    },
    
    fire: function() {
        var theta = this.actor.getHeadingRadians();
        this.vx -= Math.cos(theta) * this.bulletSpeed / 20;
        this.vy -= Math.sin(theta) * this.bulletSpeed / 20;

        // Oops, this shouldn't be like this look away!... (it should be a simple "create" method call, which doesn't exist yet).
        var actor = new itchy.Actor(game.resources.getCostume("bullet"));
        var behaviour = game.resources.scriptManager.createBehaviour( new itchy.util.ClassName( "Bullet.js" ) );
        actor.setBehaviour( behaviour );
        this.actor.getLayer().addTop(actor);
        // Ok, the nasty code has gone, you can look again ;-)

        behaviour.behaviourScript.speed = this.bulletSpeed;
        actor.setDirection( this.actor.getHeading() );
        actor.moveTo( this.actor );
        actor.moveForwards(40);
        actor.deathEvent("death");
    }
    
});
BehaviourScript.addProperty("Ship", "rotationSpeed", Double, "Rotation Speed (Deg per Tick)");
BehaviourScript.addProperty("Ship", "thrust", Double, "Thrust (Pixels per Tick)");
BehaviourScript.addProperty("Ship", "bulletSpeed", Double, "Bullet Speed (Pixels per Tick)");
BehaviourScript.addProperty("Ship", "firePeriod", Double, "Fire Period (seconds)");

