${NAME} = Class({
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
        // TODO Called 50 times a second, and is where all the good stuff belongs!
    }

    // Other methods include :
    // onDetach, onKill, onMouseDown, onMouseUp, onMouseMove
});
// TODO Define each editable property like so 
// RoleScript.addProperty("${NAME}", "exampleInteger", Integer, "Example Integer");
// RoleScript.addProperty("${NAME}", "exampleDouble", Double, "Example Double");
// RoleScript.addProperty("${NAME}", "exampleString", String, "Example String");
// RoleScript.addProperty("${NAME}", "exampleColour", RGBA, "Example Colour");
// Each property should be initialised to a default value in the "init" method.

