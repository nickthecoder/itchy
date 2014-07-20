from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
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
        if Itchy.getGame().getDirector().previousSceneName == self.scene :
            print "Moving player to this warp"
            player = Itchy.getGame().findRoleById("player")
            if player :
                x = self.getActor().getX() + self.exitX * Itchy.getGame().getDirector().squareSize
                y = self.getActor().getY() + self.exitY * Itchy.getGame().getDirector().squareSize
                player.getActor().moveTo( x, y )

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


