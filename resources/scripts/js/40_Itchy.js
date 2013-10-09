//Package aliases
itchy=Packages.uk.co.nickthecoder.itchy;
jame=Packages.uk.co.nickthecoder.jame;

// Import types - Used when adding properties.
Double = java.lang.Double;
Integer = java.lang.Integer;
RGBA = jame.RGBA;

//Objects
Itchy=itchy.Itchy;
game = null;
gameScript = null;
sceneBehaviour = null;
sceneBehaviourScript = null;

//Functions
include=language.loadScript;


GameScript = Class({

    getInitialSceneName: function() { return "start" },
    
    onActivate: function() {},
    
    onDeactivate: function() {},
            
    onQuit: function() { return true; },
    
    onKeyDown: function( keyEvent ) { return false; },
    
    onKeyUp: function( keyEvent ) { return false; },
    
    onMouseDown: function( mouseEvent ) { return false; },
    
    onMouseUp: function( mouseEvent ) { return false; },
    
    onMouseMove: function( mouseEvent ) { return false; },
    
    onMessage: function( message ) {},
    
    tick: function() {}
    
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
    
    tick : function() {},
    
    onAttach : function() {},
    
    onDetach : function() {}, 
    
    onActivate : function() {},
    
    onDeactivate : function() {},
    
    onKill : function() {},
    
});


SceneBehaviourScript = Class({

    Class: {
    
        addProperty: function( className, propertyName, klass, label ) {
            if (klass == String) klass = java.lang.String;
            itchy.script.ScriptedSceneBehaviour.addProperty(
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



