DropProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
        this.extraSpeed = 0;
        this.speedFactor = 1;
    }
    
});
CostumePropertiesScript.addProperty("DropProperties", "extraSpeed", Integer, "Extra Speed");
CostumePropertiesScript.addProperty("DropProperties", "speedFactor", Double, "Speed Factor");
