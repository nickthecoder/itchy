from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.role import PlainRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from gridRole import GridRole

from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import IntegerProperty

properties = ArrayList()
properties.add( StringProperty( "scene" ) )
properties.add( IntegerProperty( "exitX" ).label("Exit dx") )
properties.add( IntegerProperty( "exitY" ).label("Exit dy") )

class Warp(GridRole) :

    def __init__(self) :
        super(Warp,self).__init__()
        self.scene = "menu"
        self.exitX = 0
        self.exitY = -1
        
    def onBirth(self) :
        self.addTag("soft")
        self.addTag("warp")
        
        self.addTag("roundedNW")
        self.addTag("roundedNE")
        self.addTag("roundedSW")
        self.addTag("roundedSE")

        
        if self.isCompleted() :
            self.getActor().event("completed")

        if not Itchy.getGame().resources.getSceneResource(self.scene) :
            self.getActor().event("closed")
            self.getActor().setRole( PlainRole() )

    def onInvaded( self, invader ) :
        super(Warp,self).onInvaded(invader)
        Itchy.getGame().startScene( self.scene )

    def isCompleted(self):
        return Itchy.getGame().getPreferences().node("completed").getBoolean( self.scene, False )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


