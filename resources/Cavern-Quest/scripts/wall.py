from common import * #@UnusedWildImport

from gridRole import GridRole

properties = ArrayList()

class Wall(GridRole) :

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )

