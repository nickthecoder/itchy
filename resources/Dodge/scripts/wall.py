from common import * #@UnusedWildImport

properties = ArrayList()

class Wall(AbstractRole) :

    def __init__(self) :
        self.addTag("wall")

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


