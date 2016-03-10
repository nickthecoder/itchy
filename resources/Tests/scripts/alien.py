from common import * #@UnusedWildImport

properties = ArrayList()

class Alien(AbstractRole) :

    def __init__(self) :
        self.testValue = 0.0
        pass
        
    def onBirth(self):
        pass

    def tick(self):
        if self.testValue != 0 :
            self.actor.direction = self.testValue
            self.actor.appearance.alpha = self.testValue 


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


