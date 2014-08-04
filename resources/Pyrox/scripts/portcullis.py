from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Timer

from java.util import ArrayList

from gridRole import GridRole

from uk.co.nickthecoder.itchy.property import StringProperty

properties = ArrayList()
properties.add( StringProperty( "requires" ) )
properties.add( StringProperty( "text" ) )

class Portcullis(GridRole) :

    def __init__(self) :
        super(Portcullis,self).__init__()
        self.requires = ""
        self.text = ""
        self.timer = None

    def onAttach(self) :
        super(Portcullis,self).onAttach()
        self.addTag("portcullis")

    def getReady(self, player) :
        delay = player.getActor().distanceTo( self.getActor() ) / 200
        if self.isOpen() :
            self.timer = Timer.createTimerSeconds( delay )
            
    def tick(self) :
        if self.timer and self.timer.isFinished() :
            self.explode()
            self.getActor().kill()

    def isOpen(self):
        node = Itchy.getGame().getPreferences().node("completed")
        for requirement in self.requires.split(",") :
        
            if not node.getBoolean( requirement.strip(), False ) :
                return False

        return True

    def canShove(self,shover, dx, dy, speed, force ) :
        if shover.hasTag("player") :
            req = self.requires if self.text == "" else self.text
            shover.talk( self.actor.costume.getString("requires") + "\n" + req )
    
    
    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


