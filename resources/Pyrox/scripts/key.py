from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty

from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty( "name" ) )

class Key(GridRole) :

    def __init__(self) :
        super(Key,self).__init__()
        self.name="key"

    def onBirth(self) :
        super(Key,self).onBirth()
        
        self.addTag( "soft" )
        self.addTag( "roundedNE" )
        self.addTag( "roundedSW" )

    def onHalfInvaded(self,invader) :
        if invader.hasTag("player") :
            self.deathEvent("collected")
            for safe in Itchy.getGame().findRoleByTag("safe-" + self.name) :
                safe.unlock()
        else :
            self.explode()

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


