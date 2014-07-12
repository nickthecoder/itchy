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
        game.loadScene("foreground", true);
    },

    onKeyDown : function(event) {
        // Escape key takes us back to the menu.
        if (event.symbol == event.ESCAPE) {
            game.startScene("menu");
            return true; // Return true to indicate that the key has been
                            // processed.
        }
        // Play again if dead an return pressed.
        if ((event.symbol == event.RETURN) && (directorScript.lives == 0)) {
            directorScript.startGame();
        }

        if ((event.symbol > event.KEY_0) && (event.symbol <= event.KEY_9)) {
            game.startScene("" + (event.symbol - event.KEY_0));
        }

        if (event.symbol == event.p) {
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
