Menu = new Class({

    Extends: SceneBehaviourScript,
    
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
    
    isPlaying: function() {
        return false;
    } 
});

