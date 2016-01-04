from common import *

from movable import Movable

# Used as padding when creating Big objects. The main object is in one square, which Part objects
# fill other squares, to make up the bulk.
class Part(Movable) :

    def __init__( self, parent, dx, dy ) :
        Movable.__init__(self)

        self.parent = parent
        self.offsetx = dx
        self.offsety = dy

        self.speed = parent.speed

        self.parent.parts.append( self )

        x = parent.square.x + dx
        y = parent.square.y + dy
        self.square = parent.square.grid.getSquare(x, y)
        self.square.occupant = self

    def canShove( self, pusher, dx, dy, speed, force) :
        return self.parent.canShove(pusher, dx, dy, speed, force)
        
    def shove( self, pusher, dx, dy, speed=None ) :
        self.parent.shove(pusher, dx, dy, speed)

    def explode( self ) :
        self.parent.explode()

    def onHit( self, hitter, dx, dy ) :
        self.parent.onHit( hitter, dx, dy )

    def onExplode(self) :
        self.parent.onExplode()

    def __str__( self ) :
        if self.square :
            return "Part of " + self.parent.__class__.__name__ + "(" + str( self.offsetx ) + "," + str( self.offsety ) + ")"
        else :
            return "Part of " + self.parent.__class__.__name__
            

