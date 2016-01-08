from common import *

properties = ArrayList()

class Solid(AbstractRole) :
        
    def onBirth(self):
        self.addTag( "solid" )

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


