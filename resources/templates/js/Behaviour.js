${NAME} = Class({
    Extends: BehaviourScript,
    
    init: function() {
        // TODO Initialise your object. Note you can't access this.actor yet.
    },
    
    onAttach: function() {
        // TODO This behaviour is now attached to an actor, i.e. this.actor is now available.
    },
    
    tick: function() {
        // TODO Called 50 times a second, and is where all the good stuff belongs!
    }
    
    // Other methods include :
    // onDetach, onKill, onMouseDown, onMouseUp, onMouseMove
});
// TODO Define each editable property like so 
// BehaviourScript.addProperty("${NAME}", "exampleInteger", Integer, "Example Integer");
// BehaviourScript.addProperty("${NAME}", "exampleDouble", Double, "Example Double");
// BehaviourScript.addProperty("${NAME}", "exampleString", String, "Example String");
// BehaviourScript.addProperty("${NAME}", "exampleColour", RGBA, "Example Colour");
// Each property should be initialised to a default value in the "init" method.

