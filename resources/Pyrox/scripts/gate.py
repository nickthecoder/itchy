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
properties.add( IntegerProperty( "spare" ).label("Spare Collectables" ) )

class Gate(GridRole) :

    def __init__(self) :
        super(Gate,self).__init__()
        self.scene = "menu"
        self.exiting = False # Set in onInvaded, then used in onDeath to start the new scene
        self.spare = 0

    def onBirth(self) :
        self.addTag("roundedNE")
        self.addTag("roundedSE")
        self.addTag("roundedSW")
        self.addTag("roundedNW")
        self.addTag("gate")
        
    def onPlacedOnGrid(self) :
        super(Gate,self).onPlacedOnGrid()
        Itchy.getGame().getSceneDirector().collected( self.spare )

    def onInvaded( self, invader ) :
        super(Gate,self).onInvaded(invader)
        self.actor.deathEvent("exit")
        self.exiting = True
        invader.event("exit")
        Itchy.getGame().getPreferences().node("completed").putBoolean( Itchy.getGame().getSceneName(), True )

    def onDeath( self ) :
        if self.exiting :
            Itchy.getGame().getDirector().returnToGateRoom( self.scene )
        

    def onMessage( self, message ) :
        if (message == "open") :
            self.getActor().event("open")
            self.addTag("soft")


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


