from common import * #@UnusedWildImport
from gridRole import GridRole

properties = ArrayList()

class Ore(GridRole) :

    def onBirth(self) :
        super(Ore,self).onBirth()
        self.addTag("soft")

    def onInvaded(self, invader) :
        print "Collected ore"
        self.deathEvent("collect")
        shipActor = Itchy.getGame().findActorById("ship")
        if shipActor :
            shipActor.role.addTag( "soft" )
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


