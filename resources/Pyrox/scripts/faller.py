from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

import gridRole
from gridRole import GridRole
from movable import Movable

from java.util import ArrayList

properties = ArrayList()

class Faller(Movable) :

    def onBirth( self ) :
        super(Faller,self).onBirth()
        self.speed = 10
        # Keep note if the faller was just pushed, or if its had a time to move on its own / been idle.
        # Used to determin if the faller can be pushed if there is clear air below it.
        self.pushed = False

        # Determines the min force needed to shove this.
        self.weight = 1
        
        # Does this object roll off of rounded objects?
        self.rolls = True
        self.addTag("faller")
            
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
            if (self.lookEast().isEmpty() and self.lookSouthEast(self.speed/2).hasTag("squashS")) :
                self.getActor().event("rollClockwise")
                self.moveEast()
                return


        if (south.hasTag("roundedNW")) :
            if (self.lookWest().isEmpty() and self.lookSouthWest(self.speed/2).hasTag("squashS")) :
                self.getActor().event("rollAnticlockwise")
                self.moveWest()
                return
        
    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
        
        if (force < self.weight) :
            return False

        # If we've been pushed, then check to see if we can fall under gravity, if so, we can't be pushed.
        # If we haven't been pushed, then we ignore the fact that we can drop, and allow ourselves to be pushed.
        # This allows things to push pushed ONCE (i.e. across just ONE square) before gravity causes us to fall.
        if self.pushed :
            south = self.lookSouth()
            if south.hasTag("squashS") :
                return False
                
            if self.rolls :

                # Don't roll over moving objects.
                if south.isMoving() :
                    return False

                if (south.hasTag("roundedNE")) :
                    if (self.lookEast().isEmpty() and self.lookSouthEast(self.speed/2).hasTag("squashS")) :
                        return False

                if (south.hasTag("roundedNW")) :
                    if (self.lookWest().isEmpty() and self.lookSouthWest(self.speed/2).hasTag("squashS")) :
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
                south.onHit( self, dx, dy )



    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


