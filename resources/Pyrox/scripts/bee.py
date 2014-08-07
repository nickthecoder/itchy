from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Timer

from uk.co.nickthecoder.itchy.property import IntegerProperty
from uk.co.nickthecoder.itchy.property import ChoiceProperty

from java.util import ArrayList
from java.util import Random

from movable import Movable

properties = ArrayList()
properties.add( ChoiceProperty( "logic" ).add("Random", 0).add("Clockwise", 1).add("Anticlockwise", 2) )

class Bee(Movable) :

    def __init__(self) :
        super(Bee,self).__init__()
        self.random = Random()
        self.direction = -1
        self.initialTimer = Timer.createTimerSeconds(0.4)
        self.logic = 0
        self.direction = 1

    def onBirth(self) :
        super(Bee,self).onBirth()
        self.addTag("pollinator")
        self.addTag("deadly")
        self.addTag("soft")
        
    def tick( self ) :
        if self.initialTimer is None or self.initialTimer.isFinished() :
            self.initialTimer = None
                
            Movable.tick(self)

            if not self.isMoving() :
            
                if self.logic == 1 :
                    self.moveRound( 1 )
                elif self.logic == 2 :
                    self.moveRound( -1 )
                else :
                    self.makeRandomMove()
            
    def changeDirection( self, delta ) :
        self.direction += delta
        if self.direction < 0 :
            self.direction += 4
        elif self.direction > 3 :
            self.direction -= 4;
   
    def moveRound( self, delta ) :

        # Look left first, then forwards, then right and then behind
        # Note if delta is -1, then it is right, forwards, left, behind.
        self.changeDirection( -delta )  
        for i in range( 0, 4 ) :
            if self.moveForwards() :
                return
            self.changeDirection( delta )
           
    
    def moveForwards( self ) :
    
        forward = self.lookDirection( self.direction )
        if self.canMove(forward) :
            self.moveDirection( self.direction )
            return True
            
        return False

    def makeRandomMove(self) :
    
        if self.random.nextInt( 20 ) == 1 :
            if self.tryToMove( 0, -1 ) :
                return
                
        if self.random.nextInt( 20 ) == 1 :
            if self.tryToMove( 0, 1 ) :
                return
        
        if self.random.nextInt( 20 ) == 1 :
            self.direction = -self.direction

        if not self.tryToMove( self.direction, 0 ) :
            self.direction = -self.direction
            self.tryToMove( self.direction, 0 )

    def tryToMove( self, dx, dy ) :
        forward = self.look(dx, dy)
        if self.canMove( forward ) :
            self.move(dx, dy)
            if dx != 0 :
                self.event( "left" if dx == -1 else "right" )
            else :
                self.event( "up" if dy == 1 else "down" )
            return True
            
        return False
        
    def canMove(self, forward ) :
        if forward.hasTag("player") :
            return True

        return forward.isEmpty() or forward.hasTag("flower")
    

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


