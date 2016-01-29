from common import *

properties = ArrayList()

class DoorMat(AbstractRole) :

    def __init__(self) :
        self.door = None
        self.pickable = False
        
    def onBirth(self):
        self.addTag("clickable")
        self.actor.appearance.alpha = 0

    def tick(self):
        pass

    def click( self ) :
        if self.door.locked :
            if self.pickable :
                self.door.unlock()
        else :
            self.door.click()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


