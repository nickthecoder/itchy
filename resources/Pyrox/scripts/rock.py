from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

import gridRole
from gridRole import GridRole

from java.util import ArrayList

properties = ArrayList()

class Rock(GridRole) :

    def onBirth( self ) :
        # TODO Make self a property???
        self.speed = 16
        # Keep note if the rock was just pushed, or if its had a time to move on its own / been idle.
        # Used to determin if the rock can be pushed if there is clear air below it.
        self.pushed = False

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )

            
    def tick( self ) :
    
        if (not self.isMoving()) :

            self.makeAMove()
            
        super(Rock,self).tick()

    
    def makeAMove( self ) :
    
        self.pushed = False
        
        south = self.lookSouth()
        if (south.hasTag("squashS")) :
            self.moveSouth()
            return
        
        if (south.hasTag("roundedNE")) :
            if (self.lookEast().isEmpty() and self.lookSouthEast().hasTag("squashS")) :
                self.moveEast()
                return

        if (south.hasTag("roundedNW")) :
            if (self.lookWest().isEmpty() and self.lookSouthWest().hasTag("squashS")) :
                self.moveWest()
                return
         
    
    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
        
        if (force < 4) :
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
        super(Rock,self).shove(pusher, dx, dy, speed)
    
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


