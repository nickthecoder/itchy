import("GridRole.js");

Soft = Class({

    Extends: GridRole,
    
    roleName: "Soft",
    
    onAttach: function() {
        Super();
        this.role.addTag("soft");
    },
    
    pushed: function(pusher, dx, dy, force) {
        return true;
    },
    
    onInvaded: function( invader ) {
        this.actor.kill();
    }

});

