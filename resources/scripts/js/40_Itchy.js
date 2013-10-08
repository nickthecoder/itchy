//Package aliases
itchy=Packages.uk.co.nickthecoder.itchy;
jame=Packages.uk.co.nickthecoder.jame;

//Objects
Itchy=itchy.Itchy;
game = null;
gameScript = null;
sceneBehaviour = null;
sceneBehaviourScript = null;

//Functions
include=language.loadScript;


GameScript = new Class()  ({

    onActivate: function() {},
    
    getInitialSceneName: function() { return "start" },
    
    tick: function() {}
    
});



BehaviourScript = new Class() ({

    tick : function() {},
    
    onAttach : function() {},
    
    onDetach : function() {}, 
    
    onActivate : function() {},
    
    onDeactivate : function() {},
    
    onKill : function() {},
    
});
BehaviourScript.declareProperty = function( className, propertyName, label, defaultValue, klass ) {
    itchy.script.ScriptedBehaviour.declareBehaviourProperty(
        className, propertyName, label, defaultValue, klass
    );
};
BehaviourScript.stringProperty = function( className, propertyName, label, defaultValue ) {
    BehaviourScript.declareProperty( className, propertyName, label, defaultValue, java.lang.String );
};
BehaviourScript.integerProperty = function( className, propertyName, label, defaultValue ) {
    BehaviourScript.declareProperty( className, propertyName, label, defaultValue, java.lang.Integer );
};
BehaviourScript.doubleProperty = function( className, propertyName, label, defaultValue ) {
    BehaviourScript.declareProperty( className, propertyName, label, defaultValue, java.lang.Double );
};
BehaviourScript.rgbaProperty = function( className, propertyName, label, defaultValue ) {
    BehaviourScript.declareProperty( className, propertyName, label, defaultValue, jame.RGBA );
};


SceneBehaviourScript = new Class()  ({

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



