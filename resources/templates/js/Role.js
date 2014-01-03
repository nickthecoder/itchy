${NAME} = Class({
    Extends: RoleScript,
    
    init: function() {
        // TODO Initialise your object. Note you can't access this.actor yet.
    },
    
    onBirth: function() {
        // Called soon after the actor is created and after it has been placed on a Stage.
    },

    onAttach: function() {
        // TODO This role is now attached to an actor. Similar to onBirth, but if an Actor changes Roles, then onBirth will only be
        // called once, whereas onAttach is called when the Actor's role is first set, and also whenever it is changed to a different Role.
    },

    tick: function() {
        // TODO Called 60 times a second, and is where all the good stuff belongs!
    },
    
    // Boiler plate code - no need to alter it.
    getProperties: function() {
        return ${NAME}.properties;
    }

    // Other methods include :
    // onDetach, onKill, onMouseDown, onMouseUp, onMouseMove
});

${NAME}.properties = new java.util.ArrayList();
// TODO Define each editable property like so :
// ${NAME}.properties.add( new itchy.property.StringProperty( "My String", "myString" ) );
// ${NAME}.properties.add( new itchy.property.IntegerProperty( "My Integer", "myInteger" ) );
// ${NAME}.properties.add( new itchy.property.DoubleProperty( "My Double", "myDouble" ) );
// ${NAME}.properties.add( new itchy.property.BooleanProperty( "My Boolean", "myBoolean" ) );
// ${NAME}.properties.add( new itchy.property.RGBAProperty( "My Colour", "myColor", false, false ) );
// Each property should be initialised to a default value in the "init" method.

