from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from gridRole import GridRole

from java.util import ArrayList


properties = ArrayList()

class Collectable(GridRole) :

    def onBirth(self) :
        super(Collectable,self).onBirth()
        Itchy.getGame().getSceneDirector().collectablesRemaining += 1
        self.addTag("soft")
        
        self.getActor().getCostume().getProperties().update(self)

    def onInvaded( self, invader ) :
        if (invader.hasTag("player")) :
            Itchy.getGame().getSceneDirector().collected(1)

        self.actor.kill()


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


