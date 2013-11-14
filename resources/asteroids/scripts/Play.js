// The SceneBehaviourScript while playing the game - there is a different SceneBehaviourScript for the menus (Menu.js)
Play = new Class({

    Extends: SceneBehaviourScript,
    
    init: function()
    {
        this.score = 0;
        this.rocks = 0; // count the rocks on the screen, when it goes down to zero, next level!
        this.endTimer = null;
        this.ship = null; // Set by Ship's onBirth.
    },
    
    onKeyDown: function(ke)
    {
        // Escape key takes us back to the menu.
        if (ke.symbol == ke.ESCAPE) {
            game.startScene("menu");
            return true; // Return true to indicate that the key has been processed.
        }
        return false;
    },
    
    addRocks:function( diff )
    {
    	this.rocks += diff;
    	if (this.rocks == 0) {
			if ((this.ship != null) && (this.ship.actor.isAlive())) {
    			this.endTimer= new itchy.extras.Timer.createTimerSeconds(3);
    			this.ship.warp();
    	    }
    	}
    },
    
    tick: function()
    {
    	if (this.endTimer && this.endTimer.isFinished()) {
    		var nextScene = 1 + parseInt(game.getSceneName());
    		if (game.hasScene( nextScene )) {
    			game.startScene( nextScene );
    		} else {
    			game.startScene("completed");
    		}
    	}
    }
    
});

