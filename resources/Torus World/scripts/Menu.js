// The SceneDirectorScript for the main "menu" scene, and the "about" scene.
Menu = new Class({

    Extends : SceneDirectorScript,

    onActivate : function() {
        directorScript.showFancyMouse();

        this.inputStart = itchy.Input.find( "start" )
        this.inputExit = itchy.Input.find( "exit" )
        this.inputAbout = itchy.Input.find( "about" )
        this.inputEditor = itchy.Input.find( "editor" )
    },

    onKeyDown : function(event) {
    
        if (this.inputStart.matches(event)) {
            directorScript.startGame();

        } else if (this.inputExit.matches(event)) {
            game.startScene("menu");

        } else if (this.inputAbout.matches(event)) {
            game.startScene("about");

        } else if (this.inputEditor.matches(event)) {
            game.startEditor();
        }
    },

    // The menu has flying rocks, but we don't care about them, so do nothing.
    addRocks : function(diff) {
    }

});
