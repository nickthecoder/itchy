RockProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
    	this.pieces = 3;
    	this.points = 10;
    	this.strength = 0;
    	this.hitsRequired = 1;
    }
    
});
// TODO Define each property like so 
CostumePropertiesScript.addProperty("RockProperties", "pieces", Integer, "Pieces");
CostumePropertiesScript.addProperty("RockProperties", "points", Integer, "Points");
CostumePropertiesScript.addProperty("RockProperties", "strength", Integer, "Strength");
CostumePropertiesScript.addProperty("RockProperties", "hitsRequired", Integer, "Hits Required");
