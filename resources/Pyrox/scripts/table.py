from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

import gridRole
from gridRole import GridRole

properties = ArrayList()

class Table(GridRole) :

    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
          
        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy) ) :
            return True

        return False

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


