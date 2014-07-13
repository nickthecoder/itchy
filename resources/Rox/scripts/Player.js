import("GridRole.js");

Player = Class({

    Extends: GridRole,
    
    roleName: "Soft",

    tick: function() {
    
        if (!this.isMoving()) {

            // TODO Make this a property???
            this.speed = 3;
            
            if (Itchy.isKeyDown(Keys.LEFT)) {
                this.moveWest();
            } else if (Itchy.isKeyDown(Keys.RIGHT)) {
                this.moveEast();
            } else if (Itchy.isKeyDown(Keys.UP)) {
                this.moveNorth();
            } else if (Itchy.isKeyDown(Keys.DOWN)) {
                this.moveSouth();
            }

        }
        Super();
    },
    
    onInvading: function() {
    }

});

stdout.println( "Object : " + Object );

