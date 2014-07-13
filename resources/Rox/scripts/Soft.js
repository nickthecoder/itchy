import("GridRole.js");

Soft = Class({

    Extends: GridRole,
    
    roleName: "Soft",
    
    pushed: function(pusher, dx, dy, force) {
        return true;
    },
    
    onInvaded: function( invader ) {
        this.actor.kill();
    }

});

