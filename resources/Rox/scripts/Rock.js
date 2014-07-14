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
    
    onInvading: function() {
    },
    
    onArrived: function(invader, dx, dy ) {
        var south = this.lookSouth();
        if (south.role.hasTag("hittable")) {
            south.onHit( this );
        }
    }
    
});

