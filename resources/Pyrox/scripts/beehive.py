from common import *
from java.util import Random

from faller import Faller
from roundFeatures import RoundFeatures

properties = ArrayList()
properties.add( IntegerProperty( "bees" ) )
properties.add( IntegerProperty( "requiredPollen" ) )
properties.add( ChoiceProperty( "beeLogic" ).add("Random", 0).add("Clockwise", 1).add("Anticlockwise", 2) )
properties.add( ChoiceProperty( "beeDirection" ).add("Random", -1).add("North", 1).add("East", 0).add("South", 3).add("West",2) )
properties.add( DoubleProperty( "spawnPeriod" ).hint("seconds") )
properties.add( IntegerProperty( "randomSeed" ) )

# Initially attached to something, and therefore won't fall or roll.
# However, if it is hit or pushed, then it will fall.
# On being detached, it will emit bees. Afterwards, it becomes collectable (soft tag).

game = Itchy.getGame()

class Beehive(Faller) :

    def __init__(self) :
        super(Beehive,self).__init__()

        self.bees = 3
        self.requiredPollen = 0
        self.beeLogic = 0
        self.beeDirection = 0
        self.randomSeed = 0
        self.spawnPeriod = 1

        self.detached = False
        self.emitTimer = None
        self.pollen = 0 # Bees will add pollen if they visit a flower (Pumpkin) and then return here.

    def onBirth(self) :
        super(Beehive,self).onBirth()
        game.sceneDirector.collectablesRemaining += 1
        self.addTag("hittable")
        self.addTag("beehive")
        
        # If the seed is zero, then start with a random seed, otherwise be predictable
        if self.randomSeed == 0 :
            self.random = Random()
        else :
            self.random = Random(self.randomSeed)

        self.costumeFeatures.update(self) # Its a roundedProperties
        self.rolls = self.hasTag("roundedSE")
        self.detached = not self.rolls # The round hive is not detatched, the square one is.
            

    def makeAMove(self) :
        if self.detached :
            super(Beehive,self).makeAMove()
            
            
    def shove( self, pusher, dx, dy, speed ) :
        self.detach()
        super(Beehive,self).shove(pusher, dx, dy, speed )


    def onHit( self, hitter, dx, dy ) :
        self.detach()


    def detach(self) :
        self.detached = True
        if self.bees == 0 :
            if self.requiredPollen <= self.pollen :
                self.addTag("soft")
        else :
            self.emitTimer = Timer.createTimerSeconds(self.spawnPeriod)
        


    def onHalfInvaded( self, invader ) :
        if self.isMoving() :
            return
            
        if self.bees == 0 :

            if (invader.hasTag("player")) :
                game.sceneDirector.collected(1)
                invader.talk( "_honey" )
                self.removeFromGrid()
                self.actor.deathEvent("collected")


    def canShove( self, pusher, dx, dy, speed, force) :
    
        if self.requiredPollen > self.pollen and pusher.hasTag("player" ) :
            pusher.talk( "_collectPollen" )
            
        return Faller.canShove( self, pusher, dx, dy, speed, force )

    def tick(self) :
        super(Beehive,self).tick()

        if self.emitTimer :
            if self.emitTimer.isFinished() :
                if not self.isMoving() :

                    if self.emitBee() :
                        self.bees -= 1
                        if self.bees > 0 :
                            self.emitTimer.reset()
                        else :
                            self.emitTimer = None
                            if self.requiredPollen <= self.pollen :
                                self.addTag("soft")


    def emitBee(self) :
        if self.beeDirection == -1 :
            direction = self.random.nextInt(4)
        else :
            direction = self.beeDirection

        if self.emitBeeDirection(direction) :
            return True
            
        return False
            
    def emitBeeDirection(self, direction) :

        dx = self.getDeltaX( direction )
        dy  = self.getDeltaY( direction )

        outside = self.look( dx, dy, 100 ) # Look VERY fast
        
        if outside.hasTag("enemySoft") :
            resources = Itchy.getGame().resources
            squareSize = self.square.grid.squareSize
            
            costume = resources.getCostume("bee")
            
            beeActor = resources.createActor( costume, self.actor.stage )
            beeActor.moveTo( self.actor.x + squareSize * dx, self.actor.y + squareSize * dy )
            
            bee = beeActor.role
            bee.placeOnGrid( self.square.grid )
            bee.logic = self.beeLogic
            delta = 1 if self.beeLogic == 2 else -1
            bee.direction = (self.beeDirection + delta) % 4
            bee.random = self.random

            bee.pause = True # Wait for the "unpause" message, from the escape animation
            # Note, the order is important. This event will change the x,y, so must be after placeOnGrid.
            beeActor.event( "escape" + ( self.getDirectionAbbreviation(direction) ), "unpause" )
                        
            return True

        else :
            self.beeDirection = (self.beeDirection + 1) % 4
            
        return False


    def addPollen(self, amount) :
        if self.pollen >= self.requiredPollen :
            return False
            
        self.pollen += amount
        if self.requiredPollen <= self.pollen :
            self.addTag("soft")

        return True


    def createCostumeFeatures(self,costume) :
        return RoundFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


