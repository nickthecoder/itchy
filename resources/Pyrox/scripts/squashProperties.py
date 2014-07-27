from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import BooleanProperty

properties = ArrayList()
properties.add( BooleanProperty( "squashN" ) )
properties.add( BooleanProperty( "squashE" ) )
properties.add( BooleanProperty( "squashS" ) )
properties.add( BooleanProperty( "squashW" ) )
properties.add( BooleanProperty( "permanent" ) )

class SquashProperties(CostumeProperties) :

    def __init__(self) :
        self.squashN = False
        self.squashE = False
        self.squashS = False
        self.squashW = False
        self.permanent = False
        
    def update( self, role ) :
        role.tag( "squashN", self.squashN )
        role.tag( "squashE", self.squashE )
        role.tag( "squashS", self.squashS )
        role.tag( "squashW", self.squashW )
        role.permanent = self.permanent
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


