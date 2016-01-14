from common import *

from movable import Movable
from dummy import Dummy
from gridRole import GridRoleCostumeProperties

properties = ArrayList()

costumeProperties = ArrayList()
costumeProperties.add( BooleanProperty( "headingLeft" ) )
        
class Plane(Movable) :

    def __init__(self) :
        super(Plane,self).__init__()
        self.direction = -1
        self.dummy = None
                
    def onBirth(self):
        super(Plane,self).onBirth()
        self.rolls = False
        self.costumeProperties.update(self)
        self.squash = "squashE" if self.direction == 1 else "squashW"
        self.speed  = 10

        self.addTag( "explosionTrigger" )
        self.addTag( "deadly" )
        
    def onPlacedOnGrid(self) :
        self.dummy = Dummy(self, -self.direction, 0)
        self.dummy.speed = self.speed
        self.dummy.addTag("roundedNW")
        self.dummy.addTag("roundedNE")


    def tick(self) :

        Movable.tick(self)

        # Check that dummy hasn't been removed from the grid (which will happen if my tick caused it to die)
        if self.dummy.square :
            self.dummy.tick()

        if self.square and not self.isMoving() :

            forward = self.look( self.direction, 0 )

            if forward.hasTag(self.squash) :
                self.move(self.direction, 0)

            
    def canShove( self, pusher, dx, dy, speed, force) :

        if (force < 4) :
            return False

        self.jumpIfNearlyMoved()
        if self.dummy :
            self.dummy.jumpIfNearlyMoved()
        
        if self.isMoving() :
            return False


        if dy == 0 and dx == -self.direction :
            forward = self.look(dx * 2, dy ) # Behind the dummy object
        else :
            forward = self.look(dx, dy) # In the direction of the push...

        if forward.isMoving() :
            return False

        squashD = "squash" + self.getCompassAbbreviation(dx, dy)
        if forward.hasTag( squashD ) :
            if dy != 0 :
                dummyForward = self.dummy.look(dx,dy)
                if dummyForward.isMoving() :
                    return False
                if not dummyForward.hasTag( squashD ) :
                    return False
                
            return True

        return False
    
    def onArrived( self, dx, dy ) :
        # Don't hit things when I've been pushed, only when I've driven forwards.
        if dx == self.direction :
            forward = self.look(dx,dy)
            if (forward.role.hasTag("hittable")) :
                forward.role.onHit( self, dx, dy )

    def onDeath(self) :
        if self.dummy :
            self.dummy.removeFromGrid()
        Movable.onDeath(self)
        
    def move(self, dx, dy,speed=None) :
        Movable.move(self,dx,dy,speed)
        # Check if dummy is still on the grid (it won't be if my movement caused us both to die).
        if self.dummy.square :
            self.dummy.move(dx, dy, speed)
        

    def createCostumeProperties(self,costume) :
        return PlaneProperties(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class PlaneProperties(GridRoleCostumeProperties) :

    def __init__(self,costume) :
        super(PlaneProperties,self).__init__(costume)
        self.headingLeft = True

    def update( self, role ) :
        role.direction = -1 if self.headingLeft else 1

    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

