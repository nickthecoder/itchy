Score = new Class({

    Extends: BehaviourScript,
    
    tick: function() {
        this.actor.getAppearance().getPose().setText( "" + sceneBehaviourScript.score );
    }
});

