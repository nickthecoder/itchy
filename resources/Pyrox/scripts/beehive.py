from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Timer

from uk.co.nickthecoder.itchy.property import IntegerProperty

from java.util import ArrayList
from java.util import Random

from faller import Faller

properties = ArrayList()
properties.add( IntegerProperty( "bees" ) )
properties.add( IntegerProperty( "randomSeed" ) )

# Initially attached to something, and therefore won't fall or roll.
# However, if it is hit or pushed, then it will fall.
# On being detached, it will emit bees. Afterwards, it becomes collectable (soft tag).

class Beehive(Faller) :

    def __init__(self) :
        super(Beehive,self).__init__()
        self.bees = 3
        self.detached = False
        self.emitTimer = None
        self.randomSeed = 0

    def onBirth(self) :
        super(Beehive,self).onBirth()
        Itchy.getGame().getSceneDirector().collectablesRemaining += 1
        self.addTag("hittable")

        # If the seed is zero, then start with a random seed, otherwise be predictable
        if self.randomSeed == 0 :
            self.random = Random()
        else :
            self.random = Random(self.randomSeed)

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )


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
        if (self.bees == 0) :
            self.addTag("soft")
        else :
            self.emitTimer = Timer.createTimerSeconds(0.5)
        


    def onHalfInvaded( self, invader ) :
        if self.bees == 0 :

            if (invader.hasTag("player")) :
                Itchy.getGame().getSceneDirector().collected(1)
                invader.talk( "_honey" )
                self.removeFromGrid()
                self.actor.deathEvent("collected")
            else :
                self.explode()


    def tick(self) :
        super(Beehive,self).tick()
        
        if self.emitTimer :
            if self.emitTimer.isFinished() :
            
                if self.emitBee() :
                    self.bees -= 1
                    if self.bees > 0 :
                        self.emitTimer.reset()
                    else :
                        self.emitTimer = None
                        self.addTag("soft")


    def emitBee(self) :
        direction = 1 # TODO Random 1 or -1

        if self.emitBeeDirection(direction) :
            return True
        #else :
        #    return self.emitBeeDirection(-direction)
        return False
            
    def emitBeeDirection(self, direction) :
    
        outside = self.look( direction, 0 )
        if outside.isEmpty() :

            resources = Itchy.getGame().resources
            costume = resources.getCostume("bee")
            bee = resources.createActor( costume, self.actor.stage )
            bee.moveTo( self.actor.x + self.square.grid.squareSize * direction, self.actor.y )
            bee.role.placeOnGrid( self.square.grid )
            bee.event( "escape" + ("L" if direction == -1 else "R" ) )

            bee.role.random = self.random
            
            return True

        return False


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


