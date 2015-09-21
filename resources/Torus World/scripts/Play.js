// The SceneDirectorScript while playing the game - there is a different SceneDirectorScript for the menus (Menu.js)
Play = new Class({

    Extends : SceneDirectorScript,

    init : function() {
        this.rocks = 0; // count the rocks on the screen, when it goes down to
                        // zero, next level!
        this.endTimer = null;
        this.ship = null; // Set by Ship's onBirth.
    },

    onActivate : function() {
        this.inputExit = itchy.Input.find( "exit" )
        this.inputRestart = itchy.Input.find( "restart" )
        this.inputContinue = itchy.Input.find( "continue" )
        this.inputPause = itchy.Input.find( "pause" )
    
        game.loadScene("foreground", true);
    },

    onKeyDown : function(event) {
        // Escape key takes us back to the menu.
        if (this.inputExit.matches(event)) {
            directorScript.startScene("menu");
            return true; // Return true to indicate that the key has been processed.
        }
        // Play again if dead an return pressed.
        if (directorScript.lives == 0) {
        
            if (this.inputContinue.matches(event)) {
                directorScript.startGame(game.getSceneName());
            }
            if (this.inputRestart.matches(event)) {
                directorScript.startGame();
            }
        }
        
        if (this.inputPause.matches(event)) {
            game.pause.togglePause();
        }

        return false;
    },

    addRocks : function(diff) {
        this.rocks += diff;
        if (this.rocks == 0) {
            if ((this.ship != null) && (this.ship.actor.isAlive())) {
                this.endTimer = new itchy.extras.Timer.createTimerSeconds(3);
                this.ship.warp();
            }
        }
    },

    tick : function() {
        if (this.endTimer && this.endTimer.isFinished()) {
            var nextScene = 1 + parseInt(game.getSceneName());
            if (game.hasScene(nextScene)) {
                game.startScene(nextScene);
            } else {
                game.startScene("completed");
            }
        }
    },

    getCollisionStrategy : function(actor) {
        return directorScript.collisionStrategy;
    }

});
