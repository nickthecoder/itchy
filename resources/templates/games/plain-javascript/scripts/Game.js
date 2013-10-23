Game = Class({

    Extends: GameScript,

    init: function() {
        // TODO Initialise your object.
    },
    
    onActivate: function() {
        // TODO Called soon after the game is first created, and when it is re-awoken,
        // for example, after returning from the editor.
    },
        
    tick: function() {
        // TODO Called 50 times a second.
        // Consider using a SceneBehaviour, rather than putting code here.
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
    // onDeactivate, onQuit, onKeyDown, onKeyUp, onMouseDown, onMouseUp, onMouseMove
    // but these are rarely used because SceneBehaviour is often a better place.
});

// NOTE. You can access this object from your other scripts using the global variable : 'gameScript'.

