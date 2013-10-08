HighScore = new Class( BehaviourScript )
({
    onAttach: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviourScript.highScore );
    }
});

