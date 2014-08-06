from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Fragment
from uk.co.nickthecoder.itchy.role import Explosion

from java.util import ArrayList

from gridRole import GridRole
import gridRole

properties = ArrayList()

class Movable(GridRole) :

    def __init__(self) :
        GridRole.__init__(self)
        self.currentSpeed = 0 # Number of pixels to move per tick when dx or dy are non zero.
        self.dx = 0 # -1, 0 or 1 depending on direction of travel
        self.dy = 0 # -1, 0 or 1 depending on direction of travel.
        self.between = 0 # Distance travelled between squares (0..squareSize)
        self.squashed = False # Set to false when started to move and set to true just before onInvaded is called.
        
        
    def tick(self):
        if self.square is None :
            return

        self.idle = True
        if (self.dx != 0) or (self.dy != 0 ) :

            self.tickMove()
            
    def tickMove(self) :
        
        self.between += self.currentSpeed

        actor = self.actor
        if actor :
            actor.moveBy( self.dx * self.currentSpeed, self.dy * self.currentSpeed )

        if not self.squashed :
            if self.between >= self.square.grid.squareSize / 2 :
                self.squashed = True
                squashing = self.findLocalRole( self.dx, self.dy )
                squashing.onHalfInvaded( self )
                # Squashing something may have caused us to die, and therefore removed from the grid.
                # Just return, because the following code assumes we ARE on the grid.
                if self.square is None :
                    return

        if self.between >= self.square.grid.squareSize :
            # Correct for any overshoot
            overshoot = self.between - self.square.grid.squareSize
            if actor :
                actor.moveBy( self.dx * - overshoot, self.dy * - overshoot )
            
            squashing = self.findLocalRole( self.dx, self.dy )
            squashing.onInvaded( self )

            # Stop the movement and occupy the new square.
            dx = self.dx # Remember the direction I was travelling as its needed after dx,dy are reset.
            dy = self.dy
            self.between = 0
            self.dx = 0
            self.dy = 0

            # We may have been removed from the grid inside onInvaded, if so, skip the remainder.
            if self.square is not None :
                self.occupySquare( self.findLocalSquare( dx, dy ) )
                self.onArrived(dx, dy)
   
    
    def occupySquare( self, newSquare ) :
        # It is possible for another GridRole to have occupied my old square before I have completely vacated it,
        # in which case, do not interfer. Otherwise, reset the occupant to the global "Empty" object.
        if self.square.occupant == self :
            self.square.occupant = None

        if newSquare is None :
            self.actor.kill()
        else :
            newSquare.occupant = self
            newSquare.entrant = None

        self.square = newSquare
        
 

    def move( self, dx, dy,speed=None) :

        self.idle = False

        if self.isMoving() :
            if self.between + self.currentSpeed >= self.square.grid.squareSize :
                # print "Forcing an extra tickMove in for", self
                self.tickMove()
            else :
                print "Already moving. Ignored.", self
                return
                
        self.currentSpeed = speed if speed else self.speed
        self.squashed = False
        self.dx = dx
        self.dy = dy
        self.between = 0
        self.findLocalSquare( dx, dy ).entrant = self
     
    
    def moveEast( self ) :
        self.move( 1, 0 )
    
    def moveSouth( self ) :
        self.move( 0, -1 )
    
    def moveWest( self ) :
        self.move( -1, 0 )
    
    def moveNorth( self ) :
        self.move( 0, 1 )
    
    def shove( self, pusher, dx, dy, speed ) :
        self.move( dx, dy, speed )

    def isEmpty( self ) :
        return False
    
    def isMoving( self ) :
        return (self.dx != 0) or (self.dy != 0)

    def jumpIfNearlyMoved( self ) :
        if self.isMoving() and self.between + self.currentSpeed >= self.square.grid.squareSize :
            self.tickMove()

    # Called after the object has finished travelling from one square to another.
    # dx,dy : The direction the invader was travelling ) {
    def onArrived( self, dx, dy ) :
        # Do nothing.
        pass

    # Called when a GridRole has been invaded.
    # At this stage, the invader still has its own square, and self has its square.
    # but unless onInvaded removes invader from the grid, then invader will take over the square.
    def onInvaded( self, invader ) :
        # Do nothing
        pass


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


def getDirectionAbreviation( dx, dy ) :
    return ["W", "", "E" ][dx + 1] + ["S", "", "N"][dy + 1]



    

