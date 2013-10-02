Menu = new Class( SceneBehaviour )
({
    onMessage: function( message ) {
        stdout.println("Message : " + message );
        if (message == "editor") {
            game.startEditor();
        }
        if (message == "quit") {
            game.end();
        }
        if (message == "easy") {
            game.startScene("easy");
        }
        if (message == "medium") {
            game.startScene("medium");
        }
        if (message == "hard") {
            game.startScene("hard");
        }
    }
});

