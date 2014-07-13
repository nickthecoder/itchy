import("GridRole.js");

EmptyGridRole = Class({

    Extends: GridRole,
    
    roleName: "EmptyGridRole",
        
    pushed: function(pusher, dx, dy, force) {
        return true;
    },
    
    isEmpty: function() {
        return true;
    },

});


EmptyGridRole.instance = new EmptyGridRole();

