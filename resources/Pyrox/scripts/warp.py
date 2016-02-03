from common import *

from gridRole import GridRole

game = Itchy.getGame()

properties = ArrayList()
properties.add( StringProperty( "scene" ) )
properties.add( IntegerProperty( "exitX" ).label("Exit dx") )
properties.add( IntegerProperty( "exitY" ).label("Exit dy") )

class Warp(GridRole) :

    def __init__(self) :
        super(Warp,self).__init__()
        self.scene = "play"
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
            self.event("completed")

        if not Itchy.getGame().resources.getScene(self.scene) :
            self.event("closed")
            self.actor.role = PlainRole()

    def onInvaded( self, invader ) :
        super(Warp,self).onInvaded(invader)
        game.director.startScene( self.scene )

    def isCompleted(self):
        return game.preferences.node("completed").getBoolean( self.scene, False )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


