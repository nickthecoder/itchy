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


    def onPlacedOnGrid(self) :
        if self.permanent :
            self.makeAlternateOccupant()


    def onInvaded( self, invader ) :
        pass

    def onHalfInvaded(self,invader):
        if not self.permanent :
            self.removeFromGrid()
            self.actor.event("fade")
    
    def shove( self, pusher, dx, dy, speed ) :
        pass
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


