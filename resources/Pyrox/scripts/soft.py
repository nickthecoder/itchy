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
    
    def onInvaded( self, invader ) :

        self.explode()
        
    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


