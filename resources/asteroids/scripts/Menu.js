// The SceneBehaviourScript for the main "menu" scene, and the "about" scene.
Menu = new Class({

    Extends: SceneBehaviourScript,
    
    onKeyDown: function( event ) {
        if (event.symbol == Keys.RETURN) {
            game.startScene("1");
        } else if (event.symbol == Keys.ESCAPE) {
            game.startScene("menu");
        }
    },
    
    // The menu has flying rocks, but we don't care about them!
    addRocks:function( diff ) {
	}
    
});

