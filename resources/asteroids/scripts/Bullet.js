Bullet = Class({
    Extends: BehaviourScript,
    
    init: function() {
        this.speed = 1;
    },
    
    tick: function() {
        this.actor.moveForwards(this.speed);
        
        if (this.actor.getX() < -10) this.actor.moveBy(820,0);
        if (this.actor.getX() > 810) this.actor.moveBy(-820,0);
        if (this.actor.getY() < -10) this.actor.moveBy(0,620);
        if (this.actor.getY() > 610) this.actor.moveBy(0,-620);
        
        var i = this.actor.pixelOverlap("shootable").iterator();
        // Shoot ONE of the rocks that the bullet is touching.
        if (i.hasNext()) {
            i.next().getBehaviour().behaviourScript.shot(this);
            this.actor.kill();
        }
    }    
});

