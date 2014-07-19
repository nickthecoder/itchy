from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from gridRole import GridRole

from uk.co.nickthecoder.itchy.property import StringProperty

properties = ArrayList()
properties.add( StringProperty( "requires" ) )

class Portcullis(GridRole) :

    def __init__(self) :
        super(Portcullis,self).__init__()
        self.requires = ""
        
    def onAttach(self) :
        super(Portcullis,self).onAttach()
        if self.isOpen() :
            self.getActor().kill()
        else :
            self.getActor().event("close")

    def isOpen(self):
        print "Getting pref : " + self.requires, Itchy.getGame().getPreferences().node("completed").getBoolean( self.requires, False )
        return Itchy.getGame().getPreferences().node("completed").getBoolean( self.requires, False )

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


