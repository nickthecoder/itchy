//Package aliases
itchy=Packages.uk.co.nickthecoder.itchy;
jame=Packages.uk.co.nickthecoder.jame;

// Import types
Double = java.lang.Double;
Integer = java.lang.Integer;
RGBA = jame.RGBA;
Keys = jame.event.Keys;

//Objects
Itchy=itchy.Itchy;
game = null;
director = null;
directorScript = null;
sceneDirector = null;
sceneDirectorScript = null;


DirectorScript = Class({

    getInitialSceneName: function() { return "start" },
    
    onStarted: function() { director.defaultOnStarted(); },
    
    onActivate: function() {},
    
    onDeactivate: function() {},
            
    onQuit: function() { return false; },
    
    onKeyDown: function( keyEvent ) { return false; },
    
    onKeyUp: function( keyEvent ) { return false; },
    
    onMouseDown: function( mouseEvent ) { return false; },
    
    onMouseUp: function( mouseEvent ) { return false; },
    
    onMouseMove: function( mouseEvent ) { return false; },
    
    onMessage: function( message ) {},
    
    tick: function() {},
    
    startScene: function( sceneName ) { return director.defaultStartScene( sceneName ); }
    
    
});

BehaviourScript = Class({

    Class: {
    
        addProperty: function( className, propertyName, klass, label ) {
            if (klass == String) klass = java.lang.String;
            itchy.script.ScriptedBehaviour.addProperty(
                className, propertyName, label, klass
            );
        },
    },
    
    onBirth : function() {},

    onDeath : function() {},

    tick : function() {},
        
    onMouseDown: function( view, mouseEvent ) { return false; },
    
    onMouseUp: function( view, mouseEvent ) { return false; },
    
    onMouseMove: function( view, mouseEvent ) { return false; },
    
    isMouseListener: function() {
        return
            (this.onMouseDown != BehaviourScript.prototype.onMouseDown) ||
            (this.onMouseUp != BehaviourScript.prototype.onMouseUp) ||
            (this.onMouseMove != BehaviourScript.prototype.onMouseMove)
    },
    
    getCostumeProperties: function() {
        return this.actor.getCostume().getProperties().values;
    }
    
});


SceneDirectorScript = Class({

    Class: {
    
        addProperty: function( className, propertyName, klass, label ) {
            if (klass == String) klass = java.lang.String;
            itchy.script.ScriptedSceneDirector.addProperty(
                className, propertyName, label, klass
            );
        },
    },
    onActivate: function() {},
    
    onDeactivate: function() {},
    
    tick: function() {},

    onMouseDown: function( mouseButtonEvent ) { return false; },

    onMouseUp: function( mouseButtonEvent ) { return false; },

    onMouseMove: function( mouseMotionEvent ) { return false; },
    
    onKeyDown: function( keyboardEvent ) { return false; },
    
    onKeyUp: function( keyboardEvent ) { return false; },

    onMessage: function( message ) {}

});



CostumePropertiesScript = Class({

    Class: {
    
        addProperty: function( className, propertyName, klass, label ) {
            if (klass == String) klass = java.lang.String;
            itchy.script.ScriptedCostumeProperties.addProperty(
                className, propertyName, label, klass
            );
        },
    }
});


    

