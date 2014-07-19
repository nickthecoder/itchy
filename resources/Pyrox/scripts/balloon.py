from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

import gridRole
from gridRole import GridRole

properties = ArrayList()

class Balloon(GridRole) :

    def onBirth( self ) :
        # TODO Make self a property???
        self.speed = 4
        # Keep note if the balloon was just pushed, or if its had a time to move on its own / been idle.
        # Used to determin if the balloon can be pushed if there is clear air above it.
        self.pushed = False

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )
            
    def tick( self ) :
    
        if not self.isMoving() :
            self.makeAMove()
            
        super(Balloon,self).tick()

    
    def makeAMove( self ) :

        self.pushed = False
        
        north = self.lookNorth()
        if north.hasTag("squashN") :
            self.moveNorth()
            
        north = self.lookNorth()
        if north.hasTag("roundedSE") :
            if self.lookEast().isEmpty() and self.lookNorthEast().hasTag("squashN") :
                self.moveEast()
                return

        if north.hasTag("roundedSW") :
            if self.lookWest().isEmpty() and self.lookNorthWest().hasTag("squashN") :
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

        # Can the balloon move on it own? In which case, don't let it be shoved.
        north = self.lookNorth()
        print "Conidering canShove", north.hasTag("squashN"), self.pushed
        if north.hasTag("squashN") and self.pushed :
            print "No"
            return False

        if ( dy == -1 ) :
            if north.hasTag("roundedSE") :
                if self.lookEast().isEmpty() and self.lookNorthEast().hasTag("squashN") :
                    return False

            if north.hasTag("roundedSW") :
                if self.lookWest().isEmpty() and self.lookNorthWest().hasTag("squashN") :
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


