CollisionTester = Class({
    Extends: RoleScript,
    
    init: function() {
        // TODO Initialise your object. Note you can't access this.actor yet.
    },
    
    onBirth: function() {
        // Called soon after the actor is created and after it has been placed on a Stage.
    },

    onAttach: function() {
        // TODO This role is now attached to an actor. Similar to onBirth, but if an Actor changes Roles, then onBirth will only be
        // called once, whereas onAttach is called when the Actor's role is first set, and also whenever it is changed to a different Role.
    },

    tick: function() {
        if (!this.role.getCollisionStrategy().collisions(this.actor,["shootable"]).isEmpty()) {
            stdout.println("Hit shootable");
            this.role.event("hit");
        }

        if (!this.role.getCollisionStrategy().collisions(this.actor,["ship"]).isEmpty()) {
            stdout.println("Hit ship");
            this.role.event("hit");
        }
    },
    
    // Boiler plate code - no need to alter it.
    getProperties: function() {
        return CollisionTester.properties;
    }

});

CollisionTester.properties = new java.util.ArrayList();

