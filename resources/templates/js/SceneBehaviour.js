${NAME} = Class({
    Extends: SceneBehaviourScript,
    
    init: function() {
        // TODO Perform simple initialisation here.
        // At this stage, none of the Actors exist, and the scene's properties haven't been set.
    },
    
    onActivate: function() {
        // TODO Called when the scene is about to start. There is also a onDeactivate method.
        // The Actors now exist, and the scene's properties have been set.
        // Use this for more complex initialisation.
    },
    
    tick: function() {
        // TODO Called 50 times a second, but is often not needed, as your Actors' Behaviours will do most of the work.
    }
    
    // Other methods include :
    // onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
});
// TODO Define each editable property like so :
// SceneBehaviourScript.addProperty("${NAME}", "exampleInteger", Integer, "Example Integer");
// SceneBehaviourScript.addProperty("${NAME}", "exampleDouble", Double, "Example Double");
// SceneBehaviourScript.addProperty("${NAME}", "exampleString", String, "Example String");
// SceneBehaviourScript.addProperty("${NAME}", "exampleColour", RGBA, "Example Colour");
// NOTE : You must initialise each property to a default value in the 'init' method.

// NOTE. You can access this object from your other scripts using the global variable : 'sceneBehaviourScript'.
