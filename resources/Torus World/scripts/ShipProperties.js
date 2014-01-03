ShipProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
        this.rotationSpeed = 5;
        this.thrust = 0.3;
        this.firePeriod = 0.2;
    },
    
    getProperties: function() {
        return ShipProperties.properties;
    }
});

ShipProperties.properties = new java.util.ArrayList();
ShipProperties.properties.add( new itchy.property.DoubleProperty("rotationSpeed") );
ShipProperties.properties.add( new itchy.property.DoubleProperty("thrust") );
ShipProperties.properties.add( new itchy.property.DoubleProperty("firePeriod") );

