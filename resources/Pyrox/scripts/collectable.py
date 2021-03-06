from common import *

from gridRole import GridRole
from roundFeatures import RoundFeatures

properties = ArrayList()

game = Itchy.getGame()

class Collectable(GridRole) :

    def onBirth(self) :
        super(Collectable,self).onBirth()
        game.sceneDirector.collectablesRemaining += 1
        self.addTag("soft")
        
        self.costumeFeatures.update(self)


    def onHalfInvaded( self, invader ) :
        super(Collectable,self).onHalfInvaded(invader)
        if (invader.hasTag("collector")) :
            game.getSceneDirector().collected(1)
            self.actor.deathEvent("collected")
        else :
            self.explode()


    def createCostumeFeatures(self,costume) :
        return RoundFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


