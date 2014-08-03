from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import IntegerProperty

properties = ArrayList()
properties.add( StringProperty( "fromCostume" ) )
properties.add( StringProperty( "toCostume" ) )
properties.add( StringProperty( "enters" ) )
properties.add( StringProperty( "exits" ) )
properties.add( IntegerProperty( "offsetX" ) )
properties.add( IntegerProperty( "offsetY" ) )

class MachineProperties(CostumeProperties) :

    def __init__(self) :
        self.fromCostume = "carR"
        self.toCostume = "carL"
        self.enters = "E"
        self.exits = "W"
        self.offsetX = 0;
        self.offsetY = 0;

    def update( self, role ) :
    
        resources = Itchy.getGame().resources

        role.fromCostume = resources.getCostume(self.fromCostume)
        role.toCostume = resources.getCostume(self.toCostume)

        role.offsetX = self.offsetX
        role.offsetY = self.offsetY
        
        if self.enters == "E" :
            role.fromDX = 1
            role.fromDY = 0
            
        if self.enters == "W" :
            role.fromDX = -1
            role.fromDY = 0
            
        if self.enters == "N" :
            role.fromDX = 0
            role.fromDY = 1
            
        if self.enters == "S" :
            role.fromDX = 0
            role.fromDY = -1
    
    
        if self.exits == "E" :
            role.toDX = 1
            role.toDY = 0
            
        if self.exits == "W" :
            role.toDX = -1
            role.toDY = 0
            
        if self.exits == "N" :
            role.toDX = 0
            role.toDY = 1
            
        if self.exits == "S" :
            role.toDX = 0
            role.toDY = -1
            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


