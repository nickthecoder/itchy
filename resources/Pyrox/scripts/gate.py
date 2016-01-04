from common import *

from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty( "scene" ) )
properties.add( IntegerProperty( "spare" ).label("Spare Collectables" ) )

game = Itchy.getGame()

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
        game.sceneDirector.collected( self.spare )

    def onInvaded( self, invader ) :
        super(Gate,self).onInvaded(invader)
        self.deathEvent("exit")
        self.exiting = True
        invader.event("exit")
        game.preferences.node("completed").putBoolean( game.sceneName, True )

    def onDeath( self ) :
        if self.exiting :
            game.director.returnToGateRoom( self.scene )
        

    def onMessage( self, message ) :
        if (message == "open") :
            self.event("open")
            self.addTag("soft")


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


