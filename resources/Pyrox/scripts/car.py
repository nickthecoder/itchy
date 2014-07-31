from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from faller import Faller
from movable import Movable
import gridRole

properties = ArrayList()

class Car(Faller) :

    def __init__(self) :
        super(Car,self).__init__()
        # -1 for left, 1 for right
        self.direction = 1
        self.driveSpeed  = 8

                        
    def onBirth(self):
        super(Car,self).onBirth()
        self.rolls = False
        self.actor.costume.properties.update(self)

        self.squash = "squashE" if self.direction == 1 else "squashW"

        self.addTag( "explosionTrigger" )
        self.addTag( "deadly" );

                
    def makeAMove(self) :

        super(Car,self).makeAMove()

        if self.isMoving() :
            return
            
        forward = self.look( self.direction, 0, self.driveSpeed )

        if forward.hasTag(self.squash) :
            self.move(self.direction, 0, self.driveSpeed)
            return
            
    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False

        if (force < 4) :
            return False

        # Will be move by ourselves (falling or rolling), if so, don't let us be pushed.
        if self.pushed :
            forward = self.look( self.direction, 0 )
            if forward.hasTag(self.squash) :
                return False

        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy) ) :
            return True

    def shove( self, pusher, dx, dy, speed ) :
        self.pushed = True
        super(Car,self).shove(pusher, dx, dy, speed)
    
         
    def onArrived( self, dx, dy ) :
        # Don't hit things when I've been pushed, only when I've fallen or driven forwards.
        if dx == self.direction or dy == -1 :
            forward = self.look(dx,dy)
            if (forward.hasTag("hittable")) :
                forward.onHit( self, dx, dy )

   
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


