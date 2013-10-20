// Somewhere to hold the CostumeProperties for drops.
// Each type of drop (blue, gold, white), can have different speeds.
// The speed of a Drop will be the scene's current speed * speedFactor.
DropProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
        this.speedFactor = 1;
    }
    
});
CostumePropertiesScript.addProperty("DropProperties", "speedFactor", Double, "Speed Factor");

