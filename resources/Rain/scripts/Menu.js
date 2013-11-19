// The SceneDirectorScript for the main "menu" scene, and the "about" scene.
Menu = new Class({

    Extends: SceneDirectorScript,
    
    onMessage: function( message ) {
        if (message == "editor") {
            game.startEditor();
        }
        if (message == "quit") {
            game.end();
        }
        if (message == "resetHighScores") {
            game.getPreferences().clear();
        }
    },
    
    // Hmm, I'm not sure this is needed any longer.
    isPlaying: function() {
        return false;
    } 
});

