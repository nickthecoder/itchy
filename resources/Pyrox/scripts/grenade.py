from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import Explosion

from java.util import ArrayList

import gridRole
from faller import Faller

properties = ArrayList()

class Grenade(Faller) :

    def __init__(self) :
        super(Grenade,self).__init__()
        self.fallen = False;
    
    def onBirth(self):
        super(Grenade,self).onBirth()
        self.getActor().getCostume().getProperties().update(self)
        
        self.addTag("hittable")
        
        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )

    def onHit( self, hitter, dx, dy ) :
        if dy == -1 :
            south = self.lookSouth()
            if south.isEmpty() :
                return # Don't explode as we are falling, so not really hit.
                
        if hitter.hasTag("explosionTrigger") :
            hitter.explode()
            self.explode()

    def explode( self ) :
        
        if self.fallen :
            a = self.lookNorth()
            b = self.lookSouth()
            Explosion(self.actor) \
                .costume("explosion").eventName("default") \
                .projectiles(10).projectilesPerTick(1) \
                .vy(-3, 3).vx( -0.5, 0.5 ) \
                .createActor()
        else :
            a = self.lookEast()
            b = self.lookWest()
            Explosion(self.actor) \
                .costume("explosion").eventName("default") \
                .projectiles(10).projectilesPerTick(1) \
                .vx(-3, 3).vy( -0.5, 0.5 ) \
                .createActor()

        super(Grenade,self).explode()
                
        if a.hasTag("explodable") :
            a.explode()

        if b.hasTag("explodable") :
            b.explode()

    def onMessage( self, message ) :
        # Called from the grenade's turning animations
        if message == "turned" :
            self.fallen = not self.fallen
            
    
    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


