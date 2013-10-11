Game = Class({

    Extends: GameScript,

    init: function() {
        // TODO Initialise your object.
    },
    
    onActivate: function() {
        // TODO Called soon after the game is first created, and when it is re-awoken,
        // for example, after returning from the editor.
    },
    
    getInitialSceneName: function() {
        // Return the name of the scene to load when the game is first started.
        return "start"
    },
        
    tick: function() {
        // TODO Called 50 times a second.
        // Consider using a SceneBehaviour, rather than putting code here.
    },
    
    onKeyDown: function( event ) {
        if (event.symbol == event.ESCAPE ) {
            game.startScene("menu");
        }
    },
    
    onMessage: function( message ) {
        if (message == "play") {
            game.startScene("play");
        }
        if (message == "menu") {
            game.startScene("menu");
        }
        if (message == "about") {
            game.startScene("about");
        }
        if (message == "editor") {
            game.startEditor();
        }
        if (message == "quit") {
            game.end();
        }
    }
    
    
    // Other methods include :
    // onDeactivate, onQuit, onKeyDown, onKeyUp, onMouseDown, onMouseUp, onMouseMove, onMessage
    // but these are rarely used because SceneBehaviour is often a better place.
});

// NOTE. You can access this object from your other scripts using the global variable : 'gameScript'.

