ShipProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
        this.rotationSpeed = 5;
        this.thrust = 0.3;
        this.firePeriod = 0.2;
    }
    
});
CostumePropertiesScript.addProperty("ShipProperties", "rotationSpeed", Double, "Rotation Speed");
CostumePropertiesScript.addProperty("ShipProperties", "thrust", Double, "Thrust");
CostumePropertiesScript.addProperty("ShipProperties", "firePeriod", Double, "Fire Period");

