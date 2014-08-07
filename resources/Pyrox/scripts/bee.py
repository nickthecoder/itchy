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
properties.add( ChoiceProperty( "direction" ).add("North", 1).add("East", 0).add("South", 3).add("West",2) )
properties.add( IntegerProperty( "randomSeed" ) )

class Bee(Movable) :

    def __init__(self) :
        super(Bee,self).__init__()
        self.randomSeed = 0
        self.logic = 0
        self.direction = 1
        self.random = None
        self.wait = False
        self.jammedCount = 0 # How many clicks I've been stuck because of another bee getting in the way.

    def onBirth(self) :
        super(Bee,self).onBirth()
        self.addTag("bee")
        self.addTag("enemy")
        self.addTag("deadly")
        self.addTag("soft")
        if self.random is None :
            if self.randomSeed == 0 :
                self.random = Random()
            else :
                self.random = Random( self.randomSeed )
        
    def tick( self ) :

        Movable.tick(self)

        if self.wait :
            return

        if not self.isMoving() :
        
            if self.logic == 1 :
                self.moveRound( 1 )
            elif self.logic == 2 :
                self.moveRound( -1 )
            else :
                self.moveRandom()
            
    def changeDirection( self, delta ) :
        self.direction = (self.direction + delta) % 4


    def moveRound( self, delta ) :

        # Look left first, then forwards, then right and then behind
        # Note if delta is -1, then it is right, forwards, left, behind.
        self.changeDirection( -delta )  
        for i in range( 0, 4 ) :
            if self.tryToMoveForwards() :
                return
            self.changeDirection( delta )
           
    
    def moveRandom(self) :
    
        if self.random.nextInt( 20 ) == 0 :
            self.changeDirection(-1)

        elif self.random.nextInt( 20 ) == 0 :
            self.changeDirection(1)

        if not self.tryToMoveForwards() :
            self.changeDirection(self.direction + self.random.nextInt(3) + 1)
            
        self.tryToMoveForwards()
        # We only have two goes at moving forward!

    def tryToMoveForwards( self ) :
    
        forward = self.canMove()
        if forward :
            self.moveDirection( self.direction )
            self.jammedCount = 0
            return True

        elif self.lookDirection( self.direction ).hasTag("bee") :
            # If another bee is right in front of us, don't move. This will prevent bees moving
            # disrupting their (anti-)clockwise pattern.
            self.jammedCount += 1
            if self.jammedCount > 60 :
                self.jammedCount = 0
                return False
            return True 

        return False

    def onMessage( self, message ) :
        if message == "go" :
            self.wait = False
        
    def canMove(self) :
        self.event( "face-" + self.getDirectionAbbreviation(self.direction) )
        forward = self.lookDirection( self.direction )
        if forward.hasTag("enemySoft") :
            return True
            
        return forward.hasTag("squash" + self.getDirectionAbbreviation(self.direction))

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


