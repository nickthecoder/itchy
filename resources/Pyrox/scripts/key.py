from common import *

from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty( "name" ) )

game = Itchy.getGame()

class Key(GridRole) :

    def __init__(self) :
        super(Key,self).__init__()
        self.name="key"

    def onBirth(self) :
        super(Key,self).onBirth()
        
        self.addTag( "soft" )
        self.addTag( "roundedNE" )
        self.addTag( "roundedSW" )

    def onHalfInvaded(self,invader) :
        if invader.hasTag("player") :
            self.deathEvent("collected")
            for safe in game.findRolesByTag("safe-" + self.name) :
                safe.unlock()
        else :
            self.explode()


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


