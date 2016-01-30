from common import *

properties = ArrayList()

class Torch(AbstractRole) :

    def __init__(self) :
        self.owner = None
        
    def onBirth(self):
        self.light = self.actor.createCompanion("light").role
        self.light.torch = self

    def tick(self):
        # Follow the guard
        self.actor.moveTo( self.owner.actor )
        self.actor.moveForwards( 9, -16 )

            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


