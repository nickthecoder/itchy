Game = Class({

    Extends: GameScript,
    
    startScene: function( sceneName ) {
		var transition = itchy.extras.SceneTransition.fade();
    	if (game.getSceneName() == "menu") {
    		transition = itchy.extras.SceneTransition.slideRight();
    	}
    	if ( sceneName == "menu" )  {
    		transition = itchy.extras.SceneTransition.slideLeft();
    	}
        return new itchy.extras.SceneTransition(transition).transition(sceneName);
    }
});

