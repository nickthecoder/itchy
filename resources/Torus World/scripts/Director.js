Director = Class({

    Extends: DirectorScript,
    
    init: function() {
    	this.score = 0;
    	this.lives = 0;
    },
    
    startScene: function( sceneName ) {
    	stdout.println("Starting scene : " + sceneName );
    	if (game.pause.isPaused()) {
            game.pause.unpause();
        }
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
    		this.startGame("1");
    	}
    	
    	if (message == "continue") {
    		this.startGame(game.getSceneName());
    	}
    },
    
    startGame: function(sceneName) {
        if (!sceneName) {
            sceneName = "1";
        }
		this.score = 0;
		this.lives = 3;
		this.startScene(sceneName);    	
    },
    
    addPoints: function( points ) {
    	this.score += points;
    },
    
    showFancyMouse: function() {
    	var mousePointer = new itchy.extras.SimpleMousePointer("mouse");
    	game.mouse.setMousePointer( mousePointer );
    	new itchy.role.Explosion(mousePointer.getActor())
    		.forever().follow().offset(40,-33).projectilesPerTick(1).spread(-20,-80).distance(10)
    		.randomSpread().speed(1,2,0,0).fade(3).eventName("spark")
    		.createActor();
    }
    
});

