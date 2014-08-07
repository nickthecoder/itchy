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
            if south.isEmpty() or south.hasTag("squashS") :
                return # Don't explode as we are falling, so not really hit.
                
        if hitter.hasTag("explosionTrigger") :
            hitter.onExplode()
            self.onExplode()

    def onExplode( self ) :
        
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

        self.explode()
                
        if a.role.hasTag("explodable") :
            a.role.onExplode()
            
        if a.alternateRole and a.alternateRole.hasTag("explodable") :
            a.alternateRole.onExplode()
            
        if b.role.hasTag("explodable") :
            b.role.onExplode()

        if b.alternateRole and b.alternateRole.hasTag("explodable") :
            b.alternateRole.onExplode()
            

    def onMessage( self, message ) :
        # Called from the grenade's turning animations
        if message == "turned" :
            self.fallen = not self.fallen

            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


