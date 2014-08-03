
from movable import Movable

class Dummy(Movable) :

    def __init__(self, parent, dx, dy) :

        super(Dummy,self).__init__()
        
        self.parent = parent
        self.speed = parent.speed

        x = parent.square.x + dx
        y = parent.square.y + dy
        self.square = parent.square.grid.getSquare(x, y)
        self.square.occupant = self
        self.addTag("explodable")


    def canShove( self, pusher, dx, dy, speed, force) :
        return self.parent.canShove(pusher, dx, dy, speed, force)
        
    def shove( self, pusher, dx, dy, speed=None ) :
        self.parent.shove(pusher, dx, dy, speed)
        
    def explode( self ) :
        self.parent.explode()

