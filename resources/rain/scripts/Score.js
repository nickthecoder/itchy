Score = new Class( BehaviourScript )
({
    tick: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviourScript.score );
    }
});

