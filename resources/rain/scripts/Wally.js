Wally = Class({
    Extends: BehaviourScript,

    // Poke (mouse click) the wally walking across out view, to make him/her jump.   
    onMouseDown: function(event) {
        if (this.actor.pixelOverlap(event.x, event.y)) {
            this.actor.event("boo", itchy.Actor.AnimationEvent.PARALLEL);
        }
    }

});

