Wally = Class({
    Extends: RoleScript,

    onBirth: function() {
        stdout.println( "wally born" );
    },
    
    // Poke (mouse click) the wally walking across out view, to make him/her jump.   
    onMouseDown: function(view, event) {
        if (this.actor.pixelOverlap(event.x, event.y)) {
            this.actor.event("boo", itchy.Actor.AnimationEvent.PARALLEL);
        }
    }

});

