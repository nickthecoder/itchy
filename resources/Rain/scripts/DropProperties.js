// Somewhere to hold the CostumeProperties for drops.
// Each type of drop (blue, gold, white), can have different speeds.
// The speed of a Drop will be the scene's current speed * speedFactor.
DropProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
        this.speedFactor = 1;
    },
    
    
    // Boiler plate code - no need to alter it.
    getProperties: function() {
        return DropProperties.properties;
    }

});
DropProperties.properties = new java.util.ArrayList();
DropProperties.properties.add( new itchy.property.DoubleProperty("speedFactor") );

