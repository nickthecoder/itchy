//Package aliases
itchy=Packages.uk.co.nickthecoder.itchy;
jame=Packages.uk.co.nickthecoder.jame;

//Objects
Itchy=itchy.Itchy;
sceneBehaviour = null;

//Functions
include=language.loadScript;


Game = new Class()  ({

    onActivate: function() {},
    
    getInitialSceneName: function() { return "start" }
});



Behaviour = new Class() ({

    tick : function() {},
    
    onAttach : function() {},
    
    onDetach : function() {}, 
    
    onActivate : function() {},
    
    onDeactivate : function() {},
    
    onKill : function() {},
    
});
Behaviour.declareProperty = function( className, propertyName, label, defaultValue, klass ) {
    itchy.script.ScriptedBehaviour.declareBehaviourProperty(
        className, propertyName, label, defaultValue, klass
    );
};
Behaviour.stringProperty = function( className, propertyName, label, defaultValue ) {
    Behaviour.declareProperty( className, propertyName, label, defaultValue, java.lang.String );
};
Behaviour.integerProperty = function( className, propertyName, label, defaultValue ) {
    Behaviour.declareProperty( className, propertyName, label, defaultValue, java.lang.Integer );
};
Behaviour.doubleProperty = function( className, propertyName, label, defaultValue ) {
    Behaviour.declareProperty( className, propertyName, label, defaultValue, java.lang.Double );
};
Behaviour.rgbaProperty = function( className, propertyName, label, defaultValue ) {
    Behaviour.declareProperty( className, propertyName, label, defaultValue, jame.RGBA );
};


SceneBehaviour = new Class()  ({

    onActivate: function() { sceneBehaviour = this; },
    
    onDeactivate: function() { sceneBehaviour = null; },
    
    tick: function() {},

    onMouseDown: function( mouseButtonEvent ) { return false; },

    onMouseUp: function( mouseButtonEvent ) { return false; },

    onMouseMove: function( mouseMotionEvent ) { return false; },
    
    onKeyDown: function( keyboardEvent ) { return false; },
    
    onKeyUp: function( keyboardEvent ) { return false; },

    onMessage: function( message ) {}

});



