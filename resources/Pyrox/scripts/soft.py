from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from gridRole import GridRole

from java.util import ArrayList

properties = ArrayList()

class Soft(GridRole) :
    
    def onAttach( self ) :
        super(Soft,self).onAttach()
        self.addTag("soft")
    
    def onHalfInvaded( self, invader ) :
        super(Soft,self).onHalfInvaded(invader)
        self.explode()
        
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


