${NAME} = Class({

    Extends: DirectorScript,

    onStarted: function() {
    	// TODO Super will create a default stage and view. Omit this if you want to create your own stages and views instead.
    	Super();
    	// TODO Initialise your object.
    },
    
    tick: function() {
        // TODO Called 50 times a second.
        // Consider using a SceneDirector, rather than putting code here.
    }
    
    // Other methods include :
    // onActivate, onDeactivate, onQuit, onKeyDown, onKeyUp, onMouseDown, onMouseUp, onMouseMove, onMessage
    // Note : SceneDirector is often a better place to handle mouse and key events.
});

// NOTE. You can access this object from your other scripts using the global variable : 'directorScript'.

