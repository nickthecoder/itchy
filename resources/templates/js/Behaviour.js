${NAME} = Class({
    Extends: RoleScript,
    
    init: function() {
        // TODO Initialise your object. Note you can't access this.actor yet.
    },
    
    onAttach: function() {
        // TODO This role is now attached to an actor, i.e. this.actor is now available.
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

