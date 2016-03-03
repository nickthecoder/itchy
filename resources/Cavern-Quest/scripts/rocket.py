from common import * #@UnusedWildImport
from gridRole import GridRole

properties = ArrayList()

class Rocket(GridRole) :
        
    def onHalfInvaded(self, invader) :
        self.deathEvent( "launch" )
        invader.deathEvent( "launch" )
        ExplosionBuilder(self.actor) \
            .dependent().forever().follow().offset(0,20).projectilesPerTick(3) \
            .zOrder(10).spread(-70,-110).distance(10).randomSpread().speed(1,2,0,0).fade(3).eventName("flame") \
            .create()
            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


