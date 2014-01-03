BulletProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
    	this.impulse = 1;
    	this.strength = 1;
    },

    getProperties: function() {
        return BulletProperties.properties;
    }
});

BulletProperties.properties = new java.util.ArrayList();
BulletProperties.properties.add( new itchy.property.DoubleProperty( "Impulse (recoils the ship)", "impulse" ) );
BulletProperties.properties.add( new itchy.property.DoubleProperty( "Strength", "strength" ) );

