Rock = Class({
    Extends: BehaviourScript,
    
    init: function() {
        this.rotationSpeed = 0;
        this.vx = 0;
        this.vy = 0;
        this.generation = 2;
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
        new itchy.extras.Explosion(this.actor)
            .spread( bullet.actor.getHeading() - 120, bullet.actor.getHeading() + 120 ).randomSpread()
            .speed(5,3,0,0).fade(3).distance(this.generation * 20)
            .rotate(true).pose("fragment").projectiles(5 + this.generation * 3)
            .createActor();
            
        if (this.generation > 0) {
            this.generation -= 1;

            for (var i = 0; i < 3; i ++ ) {
                // Oh no, more horrible code...
                var actor = new itchy.Actor(this.actor.getCostume(), "" + this.generation);
                var behaviour = game.resources.scriptManager.createBehaviour( new itchy.util.ClassName( "Rock.js" ) );
                actor.setBehaviour( behaviour );
                this.actor.getStage().addTop(actor);
                // Ok, the nasty code has gone, you can look again ;-)

                behaviour.behaviourScript.generation = this.generation;
                behaviour.behaviourScript.rotationSpeed = 3 * i - this.rotationSpeed;
                behaviour.behaviourScript.vx = itchy.util.Util.randomBetween( -4, 4 );
                behaviour.behaviourScript.vy = itchy.util.Util.randomBetween( -4, 4 );
                actor.setDirection( this.actor.getHeading() + i * 120 );
                actor.moveTo(this.actor);
                actor.moveForwards( this.generation * 20 );
            }
        }
        sceneBehaviourScript.addRocks(-1);
        this.actor.deathEvent("explode");
        this.behaviour.removeTag("shootable");
    }
});
BehaviourScript.addProperty("Rock", "rotationSpeed", Double, "Rotation Speed (Degrees per Tick)");
BehaviourScript.addProperty("Rock", "vx", Double, "X Velocity");
BehaviourScript.addProperty("Rock", "vy", Double, "Y Velocity");

