HighScore = new Class( Behaviour )
({
    onAttach: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviour.highScore );
    }
});

