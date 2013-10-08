HighScore = Class({
    
    Extends: BehaviourScript,
    
    onAttach: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviourScript.highScore );
    }
});

