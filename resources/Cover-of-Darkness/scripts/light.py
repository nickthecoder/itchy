from common import *

properties = ArrayList()
# TODO declare properties here. Note that you must also initialise them in __init__
# e.g.
# properties.add( StringProperty( "myString" ).hint("My green label" ) )

class Light(AbstractRole) :

    def __init__(self) :
        self.torch = None
   
    def onBirth(self) :
        self.addTag( "light" )

    def tick(self):
        self.actor.moveTo( self.torch.actor )
        self.actor.direction = self.torch.actor.direction

        # Check if the light is overlapping a wall
        scale = 1.0
        self.actor.appearance.scale = scale
        if self.collided("opaque") :
        
            # Binary chop growing/shrinking the light till it JUST touches the wall.    
            delta = 0.5
            scale = scale - delta        
            self.actor.appearance.scale = scale
            while delta > 0.02 :
                delta = delta / 2
                if self.collided("opaque") :
                    scale -= delta
                else :
                    scale += delta
                self.actor.appearance.scale = scale
            # Grow a little, so that the end is tucked under the opaque object,
            self.actor.appearance.scale *= 1.1

        if self.collided("giveaway") :
            game.sceneDirector.caught()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


