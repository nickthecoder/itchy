from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

properties = ArrayList()

class Solid(AbstractRole) :

    def __init__(self) :
        pass
        
    def onBirth(self):
        self.addTag("solid")
        self.addTag("opaque")

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


