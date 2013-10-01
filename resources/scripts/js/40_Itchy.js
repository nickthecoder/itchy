itchy = Packages.uk.co.nickthecoder.itchy;

Behaviour = new Class()  ({

    __init__ : function()
    {
        behaviour = null;
        actor = null;
    },

    tick : function() {},
    
    onAttach : function() {},
    
    onDetach : function() {}, 
    
    onActivate : function() {},
    
    onDeactivate : function() {},
    
    onKill : function() {}
    
});



SceneBehaviour = new Class()  ({

    onActivate: function() { sceneBehaviour = this; },
    
    tick: function() {},

    onMouseDown: function( mouseButtonEvent ) { return false; },

    onMouseUp: function( mouseButtonEvent ) { return false; },

    onMouseMove: function( mouseMotionEvent ) { return false; },
    
    onKeyDown: function( keyboardEvent ) { return false; },
    
    onKeyUp: function( keyboardEvent ) { return false; },

    onMessage: function( message ) {}

});

var sceneBehaviour = null;

