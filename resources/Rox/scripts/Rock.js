Rock = Class({

    Extends: GridRole,
    
    roleName: "Rock",

    onBirth: function() {
        // TODO Make this a property???
        this.speed = 16;

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
        var south = this.lookSouth();
        if (south.role.hasTag("squashS")) {
            this.moveSouth();
            return;

        }
        
        if (south.role.hasTag("roundedNE")) {
            if (this.lookEast().isEmpty() && this.lookSouthEast().role.hasTag("squashS")) {
                this.moveEast();
                return;
            }
        }

        if (south.role.hasTag("roundedNW")) {
            if (this.lookWest().isEmpty() && this.lookSouthWest().role.hasTag("squashS")) {
                this.moveWest();
                return;
            }
        }
    },
    
    shove: function(pusher, dx, dy, speed, force) {
        if (this.isMoving()) {
            return false;
        }
        
        if (dy != 0) {
            return false;
        }
        
        if (force < 4) {
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
        
        return false;
    },
    
    onInvading: function() {
    },
    
    onArrived: function( dx, dy ) {
        if (dy == -1) {
            var south = this.lookSouth();
            if (south.role.hasTag("hittable")) {
                south.onHit( this );
            }
        }
    }
    
});

