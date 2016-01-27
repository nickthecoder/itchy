from common import *

properties = ArrayList()

class Torch(AbstractRole) :

    def __init__(self) :
        self.owner = None
        
    def onBirth(self):
        self.addTag( "light" )

    def tick(self):
        # Follow the guard
        self.actor.moveTo( self.owner.actor )
        self.actor.direction = self.owner.actor.direction
        self.actor.moveForwards( 4 )

        # Check if the torch is overlapping a wall
        scale = 1.0
        self.actor.appearance.setScale( scale )
        if self.collided("opaque") :
        
            # Binary chop growing/shrinking the torch till it JUST touches the wall.    
            delta = 0.5
            scale = scale - delta        
            self.actor.appearance.setScale(scale)
            while delta > 0.02 :
                delta = delta / 2
                if self.collided("opaque") :
                    scale -= delta
                else :
                    scale += delta
                self.actor.appearance.setScale(scale)
            # Grow a little, so that the end is tucked under the opaque object,
            self.actor.appearance.scale( 1.1 )
            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


