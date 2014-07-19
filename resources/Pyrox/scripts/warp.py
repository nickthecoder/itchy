from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from gridRole import GridRole

from uk.co.nickthecoder.itchy.property import StringProperty

properties = ArrayList()
properties.add( StringProperty( "scene" ) )

class Warp(GridRole) :

    def __init__(self) :
        super(Warp,self).__init__()
        self.scene = "menu"
        
    def onBirth(self) :
        self.addTag("soft")
            
    def onInvaded( self, invader ) :
        Itchy.getGame().startScene( self.scene )


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


