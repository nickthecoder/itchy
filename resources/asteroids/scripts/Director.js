Director = Class({

    Extends: DirectorScript,
    
    init: function() {
    	this.score = 0;
    	this.lives = 0;
    },
    
    startScene: function( sceneName ) {
    	if (sceneName == "menu" && game.getSceneName() == "menu") {
    		return true;
    	}
		var transition = itchy.extras.SceneTransition.fade();
    	if (game.getSceneName() == "menu") {
    		transition = itchy.extras.SceneTransition.slideRight();
    	}
    	if ( sceneName == "menu" )  {
    		transition = itchy.extras.SceneTransition.slideLeft();
    	}
        return new itchy.extras.SceneTransition(transition).transition(sceneName);
    },

    onMessage: function(message) {
    	if (message == "start") {
    		this.startGame();
    	}
    },
    
    startGame: function() {
		this.score = 0;
		this.lives = 3;
		this.startScene("1");    	
    },
    
    addPoints: function( points ) {
    	this.score += points;
    }
    
});

