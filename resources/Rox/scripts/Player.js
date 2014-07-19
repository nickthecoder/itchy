import("GridRole.js");

Player = Class({

    Extends: GridRole,
    
    roleName: "Player",
    
    onAttach: function() {
        Super();
        this.speed = 6;
        directorScript.gridView.centerOn(this.actor);
        this.role.addTag("hittable");
        this.role.addTag("player");
    },

    tick: function() {
    
        directorScript.gridView.centerOn(this.actor);

        if (this.isMoving()) {

        } else {
            
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
        var obj = this.look( dx, dy );
        if (obj.role.hasTag( "soft" ) || obj.role.hasTag( "squash" + dir )) {
            this.move(dx, dy);
            return;
        }

        obj.shove(this, dx, dy, this.speed, 4);
    },
    
    onInvading: function() {
    },

    onHit: function( hitter ) {
        this.actor.kill();
    },
});

