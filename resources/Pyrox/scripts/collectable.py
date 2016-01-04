from common import *

from gridRole import GridRole
from roundProperties import RoundProperties


properties = ArrayList()

class Collectable(GridRole) :

    def onBirth(self) :
        super(Collectable,self).onBirth()
        Itchy.getGame().getSceneDirector().collectablesRemaining += 1
        self.addTag("soft")
        
        if isinstance(self.costumeProperties, RoundProperties) :
            self.costumeProperties.update(self)


    def onHalfInvaded( self, invader ) :
        super(Collectable,self).onHalfInvaded(invader)

        if (invader.hasTag("player")) :
            Itchy.getGame().getSceneDirector().collected(1)
            self.actor.deathEvent("collected")
        else :
            self.explode()


    def createCostumeProperties(self) :
        return RoundProperties()


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


