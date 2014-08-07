from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from gridRole import GridRole
from movable import Movable

properties = ArrayList()

class Table(Movable) :

    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
          
        if force < 4 :
            return False

        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + self.getCompassAbreviation(dx, dy) ) :
            return True

        return False


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


