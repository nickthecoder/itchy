from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

import gridRole
from gridRole import GridRole

from java.util import ArrayList

properties = ArrayList()

class Faller(GridRole) :

    def onBirth( self ) :
        super(Faller,self).onBirth()
        # Keep note if the faller was just pushed, or if its had a time to move on its own / been idle.
        # Used to determin if the faller can be pushed if there is clear air below it.
        self.pushed = False

        # Determines the min force needed to shove this.
        self.weight = 1
        
        # Does this object roll off of rounded objects?
        self.rolls = True
            
    def tick( self ) :
    
        if (not self.isMoving()) :

            self.makeAMove()
            
        super(Faller,self).tick()

    
    def makeAMove( self ) :
    
        self.pushed = False
        
        south = self.lookSouth()
        if (south.hasTag("squashS")) :
            self.moveSouth()
            return
      
        if not self.rolls :
            return
        
        if (south.hasTag("roundedNE")) :
            if (self.lookEast().isEmpty() and self.lookSouthEast().hasTag("squashS")) :
                self.getActor().event("rollClockwise")
                self.moveEast()
                return

        if (south.hasTag("roundedNW")) :
            if (self.lookWest().isEmpty() and self.lookSouthWest().hasTag("squashS")) :
                self.getActor().event("rollAnticlockwise")
                self.moveWest()
                return
        
    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
        
        if (force < self.weight) :
            return False

        south = self.lookSouth()
        if south.hasTag("squashS") and self.pushed :
            return False
  
        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy) ) :
            return True

        return False
        
    def shove( self, pusher, dx, dy, speed ) :
        self.pushed = True
        super(Faller,self).shove(pusher, dx, dy, speed)
    
    def onInvading( self ) :
        pass
    
    def onArrived( self, dx, dy ) :
        if (dy == -1) :
            south = self.lookSouth()
            if (south.hasTag("hittable")) :
                south.onHit( self )



    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


