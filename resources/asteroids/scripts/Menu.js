// The SceneBehaviourScript for the main "menu" scene, and the "about" scene.
Menu = new Class({

    Extends: SceneBehaviourScript,
    
    onKeyDown: function( event ) {
        if (event.symbol == Keys.RETURN) {
            game.startScene("play");
        } else if (event.symbol == Keys.ESCAPE) {
            game.startScene("menu");
        }
    }
});

