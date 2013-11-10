// The SceneBehaviourScript while playing the game - there is a different SceneBehaviourScript for the menus (Menu.js)
Play = new Class({

    Extends: SceneBehaviourScript,
    
    init: function()
    {
        this.score = 0;
    },
    
    onKeyDown: function(ke)
    {
        // Escape key takes us back to the menu.
        if (ke.symbol == ke.ESCAPE) {
            game.startScene("menu");
            return true; // Return true to indicate that the key has been processed.
        }
        return false;
    }
    
});

