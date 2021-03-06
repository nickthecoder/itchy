from common import *
from gridRole import GridRole
from movable import Movable

properties = ArrayList()

class Chair(Movable) :

    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.isMoving() :
            return False
        
        if force < 4 :
            return False
            
        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + self.getCompassAbbreviation(dx, dy) ) :
            return True

        return False
        
    def shove( self, pusher, dx, dy, speed ) :
        super(Chair,self).shove( pusher, dx, dy, speed )
        self.speed = speed


    # Continue moving in the direction I was pushed until I hit something.
    def onArrived( self, dx, dy ) :
        if self.canShove( self, dx, dy, self.speed, 4 ) :
            self.move( dx, dy )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


