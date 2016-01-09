from common import *

properties = ArrayList()
properties.add( IntegerProperty( "direction" ) )
properties.add( IntegerProperty( "dx" ) )
properties.add( IntegerProperty( "dy" ) )

class AnimationTest(AbstractRole) :

    def __init__(self) :
        self.eventName = "default"
        self.dx = 0
        self.dy = 0
        self.direction = 1
                
    def tick(self):
        self.actor.moveBy( self.dx, self.dy )

    def onMessage(self, message) :
        if message == "steppedUp" :
            self.actor.moveBy( self.direction * 12, 6 )
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


