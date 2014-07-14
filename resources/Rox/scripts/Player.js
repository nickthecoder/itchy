import("GridRole.js");

Player = Class({

    Extends: GridRole,
    
    roleName: "Player",
    
    onAttach: function() {
        Super();
        this.role.addTag("hittable");
    },

    tick: function() {
    
        if (!this.isMoving()) {

            // TODO Make this a property???
            this.speed = 3;
            
            if (Itchy.isKeyDown(Keys.LEFT)) {
                this.attemptToMove( -1, 0, "W" );
            } else if (Itchy.isKeyDown(Keys.RIGHT)) {
                this.attemptToMove( 1, 0, "E" );            }
             else if (Itchy.isKeyDown(Keys.UP)) {
                this.attemptToMove( 0, 1, "N" );
            } else if (Itchy.isKeyDown(Keys.DOWN)) {
                this.attemptToMove( 0, -1, "S" );
            }

        }
        Super();
    },
    
    attemptToMove: function( dx, dy, dir ) {
        if (this.canMove(dx, dy, dir)) {
            this.move(dx, dy);
        }
    },
    
    canMove: function( dx, dy, dir ) {
        var obj = this.look( dx, dy );
        if (obj.role.hasTag( "soft" ) || obj.role.hasTag( "squash" + dir )) {
            return true;
        }
        // TODO or try pushing.
    },
    
    onInvading: function() {
    },

    onHit: function( hitter ) {
        this.actor.kill();
    },
});

