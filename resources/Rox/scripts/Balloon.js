Balloon = Class({
    Extends: GridRole,
    
    roleName: "Rock",

    onBirth: function() {
        // TODO Make this a property???
        this.speed = 4;

        this.role.addTag( "roundedNE" );
        this.role.addTag( "roundedSE" );
        this.role.addTag( "roundedSW" );
        this.role.addTag( "roundedNW" );
    },
            
    tick: function() {
    
        if (!this.isMoving()) {

            this.makeAMove();
            
        }
        Super();
    },
    
    makeAMove: function() {
        var north = this.lookNorth();
        if (north.role.hasTag("squashN")) {
            this.moveNorth();
            return;

        }
        
        if (north.role.hasTag("roundedSE")) {
            if (this.lookEast().isEmpty() && this.lookNorthEast().role.hasTag("squashN")) {
                this.moveEast();
                return;
            }
        }

        if (north.role.hasTag("roundedSW")) {
            if (this.lookWest().isEmpty() && this.lookNorthWest().role.hasTag("squashN")) {
                this.moveWest();
                return;
            }
        }
    },
    
    shove: function(pusher, dx, dy, speed, force) {
        if (this.isMoving()) {
            return false;
        }
        
        if (dy == 1) {
            return false;
        }
       
        var forward = this.look(dx, dy);
        if (forward.isMoving()) {
            return false;
        }
        
        if ( forward.role.hasTag("squash" + GridRole.Class.getDirectionAbreviation(dx, dy) ) ) {
            pusher.move(dx,dy,speed);
            this.move(dx, dy,speed);
            return true;
        }
        
        if ( forward.shove(this, dx, dy, speed, 1) ) {
            pusher.move(dx,dy,speed);
            return true;
        }

        return false;
    },
        
});

Balloon.properties = new java.util.ArrayList();

