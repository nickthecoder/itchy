from common import * #@UnusedWildImport

class Dodge(AbstractDirector) :

    def __init__(self) :
        pass

    def tick(self) :
        pass

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


