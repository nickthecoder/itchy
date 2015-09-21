WallProperties = Class({
    Extends: CostumePropertiesScript,
    
    init: function() {
        this.roundedNE = false;
        this.roundedSE = false;
        this.roundedSW = false;
        this.roundedNW = false;
    },
    
    getProperties: function() {
        return WallProperties.properties;
    }
    
});

WallProperties.properties = new java.util.ArrayList();

WallProperties.properties.add( new itchy.property.BooleanProperty( "roundedNE" ).label("Rounded NE") );
WallProperties.properties.add( new itchy.property.BooleanProperty( "roundedSE" ).label("Rounded SE") );
WallProperties.properties.add( new itchy.property.BooleanProperty( "roundedSW" ).label("Rounded SW") );
WallProperties.properties.add( new itchy.property.BooleanProperty( "roundedNW" ).label("Rounded NW") );

