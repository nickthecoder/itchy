// Package aliases
itchy=Packages.uk.co.nickthecoder.itchy;
jame=Packages.uk.co.nickthecoder.jame;

// Import types
Double = java.lang.Double;
Integer = java.lang.Integer;
RGBA = jame.RGBA;
Keys = jame.event.Keys;
Rect = jame.Rect;

// Objects
Itchy=itchy.Itchy;
game = null;
director = null;
directorScript = null;
sceneDirector = null;
sceneDirectorScript = null;

// Import a new script.
function import( filename )
{
    Itchy.getGame().scriptManager.loadScript( filename );
}

DirectorScript = Class({
    
    onStarted: function() { director.superOnStarted(); },
    
    onActivate: function() { director.superOnActivate(); },
    
    onDeactivate: function() { director.superOnDeactivate(); },
            
    onQuit: function() { return director.superOnQuit(); },
    
    onKeyDown: function( event ) { return director.superOnKeyDown(event); },
    
    onKeyUp: function( event ) { return director.superOnKeyUp(event); },
    
    onMouseDown: function( event ) { return director.superOnMouseDown(event); },
    
    onMouseUp: function( event ) { return director.superOnMouseUp(event); },
    
    onMouseMove: function( event ) { return director.superOnMouseMove(event); },
    
    onMessage: function( message ) { director.superOnMessage(message) },
    
    tick: function() { director.superTick(); },
    
    startScene: function( sceneName ) { return director.superStartScene( sceneName ); }
    
});

RoleScript = Class({
    
    onBirth : function() { this.role.superOnBirth(); },
    
    onAttach : function() { this.role.superOnAttach(); },

    onDetach : function() { this.role.superOnDetach(); },

    onDeath : function() { this.role.superOnDeath(); },

    tick : function() { this.role.superTick(); },
        
    onMouseDown: function( view, event ) { return false; },
    
    onMouseUp: function( view, event ) { return false; },
    
    onMouseMove: function( view, event ) { return false; },

    isMouseListener: function() {        
        return (this.onMouseDown != RoleScript.prototype.onMouseDown) ||
               (this.onMouseUp != RoleScript.prototype.onMouseUp) ||
               (this.onMouseMove != RoleScript.prototype.onMouseMove);
    },
    
    getCostumeProperties: function() {
        return this.actor.getCostume().getProperties().costumePropertiesScript;
    },
        
    getProperties: function() { return new java.util.ArrayList(); }

});


SceneDirectorScript = Class({

    onActivate: function() {},
    
    onDeactivate: function() {},
    
    tick: function() {},

    onMouseDown: function( mouseButtonEvent ) { return false; },

    onMouseUp: function( mouseButtonEvent ) { return false; },

    onMouseMove: function( mouseMotionEvent ) { return false; },
    
    onKeyDown: function( keyboardEvent ) { return false; },
    
    onKeyUp: function( keyboardEvent ) { return false; },

    onMessage: function( message ) {},
    
    getCollisionStrategy: function( actor ) { return  itchy.collision.BruteForceCollisionStrategy.pixelCollision; },
    
    getProperties: function() { return new java.util.ArrayList(); }

});



CostumePropertiesScript = Class({

    getProperties: function() { return new java.util.ArrayList(); }

});


    

