from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import DoubleProperty

properties = ArrayList()

properties = ArrayList()
properties.add( DoubleProperty("rotationSpeed") )
properties.add( DoubleProperty("thrust") )
        
class ShipProperties(CostumeProperties) :

    def __init__(self) :
        self.rotationSpeed = 5
        self.thrust = 0.3
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


