from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

import gridRole
from gridRole import GridRole
from movable import Movable

properties = ArrayList()

class Balloon(Movable) :

    def onBirth( self ) :
        self.speed = 4
        # Keep note if the balloon was just pushed, or if its had a time to move on its own / been idle.
        # Used to determin if the balloon can be pushed if there is clear air above it.
        self.pushed = False

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )
            
    def tick( self ) :
        
        if self.isMoving() :
            super(Balloon,self).tick()
        else :
            self.makeAMove()

    
    def makeAMove( self ) :
        self.pushed = False
        
        north = self.lookNorth()
        
        if north.hasTag("squashN") :
            self.moveNorth()
            return
            
        if north.hasTag("roundedSE") :
            if self.lookEast().isEmpty() and self.lookNorthEast(self.speed/2).hasTag("squashN") :
                self.moveEast()
                return

        if north.hasTag("roundedSW") :
            if self.lookWest().isEmpty() and self.lookNorthWest(self.speed/2).hasTag("squashN") :
                self.moveWest()
                return
    
    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
            
        if (dy == 1) :
            return False

        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if self.pushed :
        
            # Can the balloon move on it own? In which case, don't let it be shoved.
            north = self.lookNorth()
            if north.hasTag("squashN")  :
                return False

            if north.hasTag("roundedSE") :
                if self.lookEast().isEmpty() and self.lookNorthEast(self.speed/2).hasTag("squashN") :
                    return False

            if north.hasTag("roundedSW") :
                if self.lookWest().isEmpty() and self.lookNorthWest(self.speed/2).hasTag("squashN") :
                    return False
            
        if forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy)) :
            return True

        if forward.canShove(self, dx, dy, speed, 1) :
            return True

        return False

    def shove( self, pusher, dx, dy, speed ) :
        self.pushed = True
        
        forward = self.look(dx, dy)
        
        if not forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy)) :
            forward.shove( self, dx, dy, speed )
            
        super(Balloon,self).shove( pusher, dx, dy, speed )


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


