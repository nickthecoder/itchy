from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

import gridRole
from faller import Faller

from java.util import ArrayList

properties = ArrayList()

class Rock(Faller) :

    def onBirth( self ) :
        super(Rock,self).onBirth()
        
        self.weight = 4

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )
        
        self.addTag( "explosionTrigger" )
        self.addTag( "deadly" );
          
    def onInvading( self ) :
        pass
    


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


