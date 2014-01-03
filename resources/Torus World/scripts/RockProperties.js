RockProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
    	this.pieces = 0;
    	this.points = 10;
    	this.strength = 0;
    	this.hitsRequired = 1;
    },
    
    getProperties: function() {
        return RockProperties.properties;
    }
});

RockProperties.properties = new java.util.ArrayList();
RockProperties.properties.add( new itchy.property.IntegerProperty( "Pieces","pieces" ) );
RockProperties.properties.add( new itchy.property.IntegerProperty( "Points", "points" ) );
RockProperties.properties.add( new itchy.property.IntegerProperty( "Strength", "strength" ) );
RockProperties.properties.add( new itchy.property.IntegerProperty( "Hits Required", "hitsRequired" ) );
