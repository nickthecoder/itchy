// The SceneDirectorScript for the main "menu" scene, and the "about" scene.
Menu = new Class({

    Extends: SceneDirectorScript,
    
    onActivate: function() {
    	directorScript.showFancyMouse();
    },
    
    onKeyDown: function( event ) {
        if (event.symbol == Keys.RETURN) {
            stdout.println("Return pressed");
            directorScript.startGame();
            
        } else if (event.symbol == Keys.ESCAPE) {
            game.startScene("menu");
            
        } else if (event.symbol == Keys.t) {
            game.startScene("testEdgeCollisions");
            
        } else if (event.symbol == Keys.a) {
        	game.startScene("about");
        }
    },
    
    // The menu has flying rocks, but we don't care about them, so do nothing.
    addRocks:function( diff ) {
	}
    
});

