Menu = new Class({

    Extends: SceneBehaviourScript,
    
    onMessage: function( message ) {
        stdout.println("Message : " + message );
        if (message == "editor") {
            game.startEditor();
        }
        if (message == "quit") {
            game.end();
        }
        if (message.indexOf("scene:" == 0)) {
            stdout.println("Starting scene : " + message.substring(6));
            game.startScene(message.substring(6));
        }
    },
    
    isPlaying: function() {
        return false;
    } 
});

