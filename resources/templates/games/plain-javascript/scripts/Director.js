Director = Class({

    Extends: DirectorScript,

    onStarted: function() {
    	// Call Super, to create a default stage and view, which takes up the whole screen.
    	Super();
    	// TODO initialise your object.
    },

    tick: function() {
        // TODO Called 50 times a second.
        // Consider using a SceneDirector, rather than putting code here.
    },
    
    onKeyDown: function( event ) {
        // No matter which scene we are on, the escape key will always return to the menu.
        if (event.symbol == event.ESCAPE ) {
            game.startScene("menu");
        }
    },
    
    onMessage: function( message ) {
    }
    
    
    // Other methods include :
    // onActivate, onDeactivate, onQuit, onKeyDown, onKeyUp, onMouseDown, onMouseUp, onMouseMove
    // Note : SceneDirector is often a better place to handle mouse and key events.
});

// NOTE. You can access this object from your other scripts using the global variable : 'directorScript'.

