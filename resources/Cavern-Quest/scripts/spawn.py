from common import * #@UnusedWildImport

from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty("foo") )

class Spawn(GridRole) :

    def __init__(self) :
        super(Spawn,self).__init__()
        self.foo = "a"

    def onBirth(self) :
        super(Spawn, self).onBirth()
        self.addTag("spawn")
        
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


