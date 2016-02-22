from common import * #@UnusedWildImport

from movable import Movable

properties = ArrayList()

class Rock(Movable) :

    def __init__(self) :
        super(Rock,self).__init__()
        self.fallCounter = 0
        self.speed = 36
        self.addTag("rock")

    def tick(self):

        if self.idle :
            south = self.lookSouth();
            if south.hasTag("squashable") :
            
                self.fallCounter -= 1
                if self.fallCounter <= 0 :
                    
                    if south.hasTag("hittable") :
                        south.hit( self.actor.x, self.actor.y - self.square.grid.squareSize )
                        
                    else :
                        self.move(0,-1)
                        # Rocks above me fall immediately
                        north = self.lookNorth()
                        if north.hasTag("rock") :
                            north.fallCounter = 0
            else :
                if not south.hasTag("rock") :
                    self.fallCounter = 30

        else :
            self.tickMove()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


