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

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )
        
        self.addTag("balloon")
            
    def tick( self ) :
        
        super(Balloon,self).tick()
        
        if self.square and not self.isMoving() :
            self.makeAMove()

    
    def makeAMove( self ) :
        
        north = self.lookNorth()
        
        if north.hasTag("squashN") :
            # Don't move north, if something can fall into that spot.
            north2 = self.look(0,2)
            if north2.isMoving() or not north2.hasTag("faller") :
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
    
        self.jumpIfNearlyMoved()

        if self.isMoving() :
            return False
            
        if (dy == 1) :
            return False

        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False
            
        if forward.hasTag("squash" + gridRole.getDirectionAbreviation(dx, dy)) :
            return True

        if forward.canShove(self, dx, dy, speed, 1) :
            return True

        return False

    def shove( self, pusher, dx, dy, speed ) :
        
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


