from common import *

properties = ArrayList()

class Brick(AbstractRole) :
        
    def onBirth(self):
        self.addTag( "ground" ) # Mings can walk on me.

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )



