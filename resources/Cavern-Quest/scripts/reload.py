from common import * #@UnusedWildImport

from gridRole import GridRole

properties = ArrayList()
properties.add( IntegerProperty( "amount" ) )

class Reload(GridRole) :

    def __init__(self) :
        super(Reload,self).__init__()
        self.amount = 25;
        self.addTag("soft")


    def onInvaded(self, invader) :
        print "Reload"
        if invader.hasTag("player") :
            Itchy.getGame().sceneDirector.blasts += self.amount
            print "collect" + str( self.amount / 25)
            self.deathEvent( "collect" + str( self.amount / 25) )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


