from common import *
from java.util import Random

from movable import Movable

properties = ArrayList()
properties.add( ChoiceProperty( "logic" ).add("Random", 0).add("AntiClockwise", 1).add("Clockwise", 2) )
properties.add( ChoiceProperty( "direction" ).add("North", 1).add("East", 0).add("South", 3).add("West",2) )
properties.add( IntegerProperty( "randomSeed" ) )

class Bee(Movable) :

    def __init__(self) :
        super(Bee,self).__init__()
        self.randomSeed = 0
        self.logic = 1
        self.direction = 1
        self.random = None
        self.jammedCount = 0 # How many clicks I've been stuck because of another bee getting in the way.
        self.pollinated = False
        self.pause = False # Pause while pollinating a flower.

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

        if self.pause :
            return

        if not self.isMoving() :
        
            if self.logic == 1 or self.logic == 2 :
                self.moveRound()
            else :
                self.moveRandom()
            
    def changeDirection( self, delta ) :
        self.direction = (self.direction + delta) % 4


    def moveRound( self ) :

        delta = 1 if self.logic == 1 else -1
        if self.tryToMoveForwards() :
            self.changeDirection( -delta )  
            return
        else :
            if self.lookDirection( self.direction, 100 ).hasTag("bee") :
                # If another bee is right in front of us, don't move. This will prevent bees moving
                # disrupting their (anti-)clockwise pattern.
                self.jammedCount += 1
                if self.jammedCount > 60 :
                    self.jammedCount = 0
                    self.logic = 3 - self.logic # Reverse direction Clockwise <-> Anticlockwise
                    self.changeDirection( -delta )
            else :
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

        return

    def onMessage( self, message ) :
        if message == "enteredHive" :
            self.actor.kill()
        
        elif message == "unpause" :
            self.pause = False
            

    def canMove(self) :
    
        self.event( "face-" + self.getDirectionAbbreviation(self.direction) )
        forward = self.lookDirection( self.direction, 100 )
        if forward.hasTag("enemySoft") :
            return True

        if (not self.pollinated) and forward.hasTag("flower") :
            self.pollinated = True
            self.event( "pollinate", "unpause" )
            self.pause = True
            return True
            
        if self.pollinated and forward.hasTag("beehive") :
            if forward.role.addPollen(1) :
                self.speed = 1 # Slow down to enter the hive.
                self.event( "enterHive", "enteredHive" ) # The bee must die before it hits the hive.
                return True
   
        return forward.hasTag("squash" + self.getDirectionAbbreviation(self.direction))

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


