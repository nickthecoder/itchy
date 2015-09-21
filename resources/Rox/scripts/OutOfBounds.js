import("GridRole.js");

OutOfBounds = Class({

    Extends: GridRole,

    roleName: "OutOfBounds",

});

OutOfBounds.instance = new OutOfBounds();
OutOfBoundsDummyRole = Class({
    hasTag: function( tag ) {
        return false;
    }
});
OutOfBounds.instance.role = new OutOfBoundsDummyRole();
