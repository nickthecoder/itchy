from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import IntegerProperty
from uk.co.nickthecoder.itchy.property import DoubleProperty

properties = ArrayList()

properties.add( IntegerProperty("pieces") )
properties.add( IntegerProperty("points") )
properties.add( IntegerProperty("strength") )
properties.add( IntegerProperty("hitsRequired") )
 
class RockProperties(CostumeProperties) :

    def __init__(self) :
    	self.pieces = 0
    	self.points = 10
    	self.strength = 0
    	self.hitsRequired = 1
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )

