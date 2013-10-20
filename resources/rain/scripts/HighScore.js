// Used to display the high score for the current level.
// The text is only updated at the beginning of the scene.
HighScore = Class({
    
    Extends: BehaviourScript,
    
    onAttach: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviourScript.highScore );
    }
});

