Score = new Class( Behaviour )
({
    tick: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviour.score );
    }
});

