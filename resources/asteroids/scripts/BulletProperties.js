BulletProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
    	this.impulse = 1;
    }
    
});
// TODO Define each property like so 
CostumePropertiesScript.addProperty("BulletProperties", "impulse", Double, "Impulse (recoils the ship)");

