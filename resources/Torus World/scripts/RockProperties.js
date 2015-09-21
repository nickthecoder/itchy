RockProperties = Class({
    Extends : CostumePropertiesScript,

    init : function() {
        this.pieces = 0;
        this.points = 10;
        this.strength = 0;
        this.hitsRequired = 1;
    },

    getProperties : function() {
        return RockProperties.properties;
    }
});

RockProperties.properties = new java.util.ArrayList();
RockProperties.properties.add(new itchy.property.IntegerProperty("pieces"));
RockProperties.properties.add(new itchy.property.IntegerProperty("points"));
RockProperties.properties.add(new itchy.property.IntegerProperty("strength"));
RockProperties.properties.add(new itchy.property.IntegerProperty("hitsRequired"));
