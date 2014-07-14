import("GridRole.js");

EmptyGridRole = Class({

    Extends: GridRole,
    
    roleName: "EmptyGridRole",

    onBirth: function() {
        Super();
        this.role.addTag("squashE");
        this.role.addTag("squashS");
        this.role.addTag("squashW");
        this.role.addTag("squashN");
    },
    
    pushed: function(pusher, dx, dy, force) {
        return true;
    },
    
    isEmpty: function() {
        return true;
    },

});

EmptyGridRole.instance = new EmptyGridRole();
EmptyDummyRole = Class({
    hasTag: function( tag ) {
        return (tag == "squashE") || (tag == "squashS") || (tag == "squashW") || (tag == "squashN")
    }
});
EmptyGridRole.instance.role = new EmptyDummyRole();



