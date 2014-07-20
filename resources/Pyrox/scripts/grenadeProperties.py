from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.property import BooleanProperty

from java.util import ArrayList

properties = ArrayList()

properties.add( BooleanProperty( "fallen" ).label( "Fallen" ) )

class GrenadeProperties(CostumeProperties) :

    def __init__(self) :
        self.fallen = False

    def update(self, role) :       
        role.fallen = self.fallen

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


