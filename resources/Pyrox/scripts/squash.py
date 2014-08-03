from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from gridRole import GridRole

properties = ArrayList()

class Squash(GridRole) :
        
    def onBirth(self):
        super(Squash,self).onBirth()
        self.actor.costume.properties.update(self)


    def onSceneCreated(self) :
        if self.permanent :
            self.makeAlternateOccupant()


    def onInvaded( self, invader ) :
        pass

    def onHalfInvaded(self,invader):
        if not self.permanent :
            self.removeFromGrid()
            self.actor.event("fade")
    
    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


