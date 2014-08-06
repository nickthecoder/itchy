from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import BooleanProperty
from uk.co.nickthecoder.itchy.property import IntegerProperty

import roundProperties
from roundProperties import RoundProperties

from java.util import ArrayList

properties = ArrayList()
properties.addAll( roundProperties.properties )
properties.add( BooleanProperty( "canExplode" ).label( "Can Explode" ) )

class WallProperties(RoundProperties) :

    def __init__(self) :
        super(WallProperties,self).__init__()
        self.canExplode = True

    def update(self, role) :
        super(WallProperties,self).update(role)
        
        role.tag( "explodable", self.canExplode )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


