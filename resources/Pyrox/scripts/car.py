from common import *

from faller import Faller
from movable import Movable
import roundFeatures
from roundFeatures import RoundFeatures

properties = ArrayList()

costumeProperties = ArrayList()
costumeProperties.addAll( roundFeatures.costumeProperties )
costumeProperties.add( BooleanProperty( "headingLeft" ) )


class Car(Faller) :

    def __init__(self) :
        super(Car,self).__init__()
        # -1 for left, 1 for right
        self.direction = 1
        self.driveSpeed  = 8

                        
    def onBirth(self):
        super(Car,self).onBirth()
        self.rolls = False
        self.costumeFeatures.update(self)

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
    
        if (force < 4) :
            return False

        self.jumpIfNearlyMoved()
            
        if self.isMoving() :
            return False


        forward = self.look(dx, dy)
        if forward.isMoving() :
            return False

        if forward.hasTag("squash" + self.getCompassAbbreviation(dx, dy) ) :
            return True
         
    def onArrived( self, dx, dy ) :
        # Don't hit things when I've been pushed, only when I've fallen or driven forwards.
        if dx == self.direction or dy == -1 :
            forward = self.look(dx,dy).role
            if (forward.hasTag("hittable")) :
                forward.onHit( self, dx, dy )

   
    def createCostumeFeatures(self,costume) :
        return CarFeatures(costume)

   
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class CarFeatures(RoundFeatures) :

    def __init__(self,costume) :
        super(CarFeatures,self).__init__(costume)
        self.headingLeft = True


    def update(self, role) :
        super(CarFeatures,self).update(role)
        
        role.direction = -1 if self.headingLeft else 1


    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties


