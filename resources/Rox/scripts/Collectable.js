import("Wall.js");

Collectable = Class({
    Extends: Wall,
    
    roleName: "Collectable",
        
    init: function() {
        Super();
        sceneDirectorScript.collectablesRemaining += 1;
    },
    
    onAttach: function() {
        Super();
        this.role.addTag("soft");
    },

    onInvaded: function( invader ) {
        if (invader.role.hasTag("player")) {
            sceneDirectorScript.collectablesRemaining -= 1;
        }
        this.actor.kill();
    },
    
    // Boiler plate code - no need to alter it.
    getProperties: function() {
        return Collectable.properties;
    }

    // Other methods include :
    // onDetach, onKill, onMouseDown, onMouseUp, onMouseMove
});

Collectable.properties = new java.util.ArrayList();

