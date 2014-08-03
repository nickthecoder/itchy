from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import Explosion

from java.util import ArrayList

from gridRole import GridRole

properties = ArrayList()

class Hard(GridRole) :

    def canShove( self, pusher, dx, dy, speed, force ) :
    
        if pusher.hasTag("digger") and not self.actor.isDying() :
            
            Explosion(self.actor) \
                .projectiles(10).projectilesPerTick(1).slow(3) \
                .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5) \
                .pose("fragment") \
                .createActor()

            self.actor.deathEvent("dig")

        return False

    
    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


