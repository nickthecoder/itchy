from common import *

properties = ArrayList()
properties.add( IntegerProperty( "direction" ) )
properties.add( DoubleProperty( "dx" ) )
properties.add( DoubleProperty( "dy" ) )

class AnimationTest(AbstractRole) :

    def __init__(self) :
        self.eventName = "default"
        self.dx = 0
        self.dy = 0
        self.direction = 1

        self.key = Input.find("step")
        self.wasDown = False

    def animate(self):
        if self.key.pressed() :
            self.wasDown = True
        else :
            if self.wasDown :
                AbstractRole.animate(self)
                AbstractRole.animate(self)
                AbstractRole.animate(self)
                AbstractRole.animate(self)
                AbstractRole.animate(self)
                self.actor.moveBy( self.dx, self.dy )
                self.wasDown = False

    def tick(self):
        self.actor.moveBy( self.dx, self.dy )
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


