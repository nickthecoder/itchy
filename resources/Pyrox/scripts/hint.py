from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty

properties = ArrayList()
properties.add( StringProperty( "hint" ) )

class Hint(AbstractRole) :

    def __init__(self) :
        self.hint=""

    def tick(self) :
        players = self.getCollisionStrategy().collisions(self.getActor(),["player"])
        for player in players :
            player.talk( self.hint )
            self.getActor().deathEvent("death")

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


