from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Fragment
from uk.co.nickthecoder.itchy.role import Explosion

from gridRole import GridRole

from java.util import ArrayList

properties = ArrayList()

class Soft(GridRole) :

    def onBirth( self ) :
        Fragment().actor(self.actor).pieces( 10 ).createPoses( "fragment" )

    
    def onAttach( self ) :
        super(Soft,self).onAttach()
        self.addTag("soft")
    
    def onInvaded( self, invader ) :

        Explosion(self.actor) \
            .projectiles(10) \
            .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5) \
            .pose("fragment") \
            .createActor() \

        self.actor.kill()


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


