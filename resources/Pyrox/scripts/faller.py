from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy import Actor
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

        # Determines the min force needed to shove this.
        self.weight = 1

        # Does this object roll off of rounded objects?
        self.rolls = True
        self.addTag("faller")
            
    def tick( self ) :
    
        super(Faller,self).tick()
        
        if self.square and not self.isMoving() :
            self.makeAMove()
    
    def makeAMove( self ) :
    
        south = self.lookSouth()
        
        if south.hasTag("squashS") :
            self.moveSouth()
            return

        if south.canShove(self,0,-1,self.speed,0) :
            south.shove(self, 0, -1, self.speed )
            self.moveSouth()
            return
            
            
        if not self.rolls :
            return

        # If south is a balloon, then don't roll round it, let it roll round me.
        if south.hasTag("balloon") :
            return
        
        if (south.hasTag("roundedNE")) :
            if (self.lookEast().isEmpty() and self.lookSouthEast(self.speed/2).hasTag("squashS")) :
                # The animation must be done in parallel with existing animations, because rolling off
                # of something straight after exiting a localGate needs the localGate's exit animation to complete
                self.getActor().event("rollClockwise", "turned", Actor.AnimationEvent.PARALLEL)
                self.moveEast()
                return


        if (south.hasTag("roundedNW")) :
            if (self.lookWest().isEmpty() and self.lookSouthWest(self.speed/2).hasTag("squashS")) :
                self.getActor().event("rollAnticlockwise", "turned", Actor.AnimationEvent.PARALLEL)
                self.moveWest()
                return
        
    def canShove( self, pusher, dx, dy, speed, force) :
    
        if (force < self.weight) :
            return False

        self.jumpIfNearlyMoved()

        if self.isMoving() :
            return False
            
        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy) ) :
            return True

        return False
        
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


