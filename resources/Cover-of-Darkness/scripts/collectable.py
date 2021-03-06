from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

properties = ArrayList()

class Collectable(AbstractRole) :

    def __init__(self) :
        pass
        
    def onBirth(self) :
        self.addTag( "collectable" )
        Itchy.getGame().getSceneDirector().collectables += 1

    def collect(self) :
        self.removeTag( "collectable" )
        self.actor.deathEvent("collected")
        return 1

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


