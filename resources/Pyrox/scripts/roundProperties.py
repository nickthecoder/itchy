from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import BooleanProperty
from uk.co.nickthecoder.itchy.property import IntegerProperty

from gridRole import GridRole

from java.util import ArrayList

properties = ArrayList()
# TODO declare properties here. Note that you must also initialise them in __init__
properties.add( BooleanProperty( "roundedNE" ).label( "Rounded NE" ) )
properties.add( BooleanProperty( "roundedSE" ).label( "Rounded SE" ) )
properties.add( BooleanProperty( "roundedSW" ).label( "Rounded SW" ) )
properties.add( BooleanProperty( "roundedNW" ).label( "Rounded NW" ) )

class RoundProperties(CostumeProperties) :

    def __init__(self) :

        self.roundedNE = False
        self.roundedSE = False
        self.roundedSW = False
        self.roundedNW = False

    def update(self, role) :

        role.tag("roundedNE", self.roundedNE)
        role.tag("roundedSE", self.roundedSE)
        role.tag("roundedSW", self.roundedSW)
        role.tag("roundedNW", self.roundedNW)

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


