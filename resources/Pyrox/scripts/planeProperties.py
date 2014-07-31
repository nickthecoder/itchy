from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import BooleanProperty

properties = ArrayList()
properties.add( BooleanProperty( "headingLeft" ) )
        
class PlaneProperties(CostumeProperties) :

    def __init__(self) :
        self.headingLeft = True

    def update( self, role ) :
        role.direction = -1 if self.headingLeft else 1

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


