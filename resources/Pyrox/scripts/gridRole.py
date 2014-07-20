from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Fragment
from uk.co.nickthecoder.itchy.role import Explosion

from java.util import ArrayList

from grid import Grid
import gridRole

properties = ArrayList()

class GridRole(AbstractRole) :

    def __init__(self) :
        self.square = None
        self.currentSpeed = 0 # Number of pixels to move per tick when dx or dy are non zero.
        self.speed = 4 # The default speed for this object.
        self.dx = 0 # -1, 0 or 1 depending on direction of travel
        self.dy = 0 # -1, 0 or 1 depending on direction of travel.
        self.between = 0 # Distance travelled between squares (0..squareSize)
        self.squashed = False # Set to false when started to move and set to true just before onInvaded is called.
        
    def onBirth( self ) :
        Fragment().actor(self.actor).pieces( 10 ).createPoses( "fragment" )
        self.addTag( "explodable" )
    
    def onAttach( self ) :
        pass
        
    def tick(self):

        if (self.dx != 0) or (self.dy != 0 ) :

            self.between += self.currentSpeed

            if not self.squashed :
                if self.between >= self.square.grid.squareSize / 2 :
                    self.squashed = True
                    squashing = self.findLocalRole( self.dx, self.dy )
                    squashing.onInvaded( self )
                        
            self.getActor().moveBy( self.dx * self.currentSpeed, self.dy * self.currentSpeed )

            if self.between >= self.square.grid.squareSize :
                # Correct for any overshoot
                overshoot = self.between - self.square.grid.squareSize
                self.getActor().moveBy( self.dx * - overshoot, self.dy * - overshoot )
                
                # Stop the movement and occupy the new square.
                dx = self.dx # Remember the direction I was travelling as its needed after dx,dy are reset.
                dy = self.dy
                self.between = 0
                self.dx = 0
                self.dy = 0
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
        
    # Called by Level.js just after a level has been loaded, and must also be called manually if you create
    # new actors on the grid dynamically.
    # Uses the actors position to calculate the correct square to occupy.
    def placeOnGrid( self, grid ) :
    
        self.square = grid.getSquareByPixel( self.getActor().getX(), self.getActor().getY() )
        if self.square != None :
            self.square.occupant = self
        else :
            print "Failed to find square! ", self.actor.getX(), ",", self.actor.getY()
            self.getActor().getAppearance().setAlpha( 128 )

    
    # Finds the square
    def findLocalSquare( self, dx, dy ) :
        x = self.square.x + dx
        y = self.square.y + dy
        return self.square.grid.getSquare(x, y)
    
    # Finds the square
    def findLocalRole( self, dx, dy ) :
        x = self.square.x + dx
        y = self.square.y + dy
        
        return self.square.grid.getGridRole(x, y)
    
    # Finds the GridRole in the given direction. If this lands us outside the whole grid, then a global "edge"
    # is returned, which is solid and unchanging. If there is nothing at that position, then a global "empty"
    # is returned.
    # When objects are moving between squares, the result is a little more complex.
    # If there is an entrant, then return that, not the occupier (which will move out, or be squashed).
    # However, if the occupant is leaving, and will be gone by the time I could enter, then don't return the occupant, 
    # and instead the global EmptyGridRole.instance.
    def look( self, dx, dy, speed=None ) :

        if speed is None :
            speed = self.speed
        
        square = self.findLocalSquare(dx, dy)
        # If there is an object entering, then this is the one we care about.
        entrant = None if (square is None) else square.entrant
        if entrant :
            return entrant
        
        occupier = self.findLocalRole( dx, dy )
        if not occupier.isMoving() :
            return occupier
        
        # Calculate how long (in frames) it will take for the occupant to leave the square, and
        # for me to enter the square.
        willLeaveTicks = (self.square.grid.squareSize - occupier.between) / occupier.speed
        willEnterTicks = self.square.grid.squareSize / speed

        # If it will leave before I get half way, then ignore it.
        if willLeaveTicks < willEnterTicks :
            return self.square.grid.getEmpty()
            
        return occupier
        
    def lookEast(self, speed=None) :
        return self.look( 1, 0, speed )
    
    def lookSouth(self, speed=None) :
        return self.look( 0,-1, speed )
    
    def lookWest(self,speed=None) :
        return self.look( -1, 0, speed )
    
    def lookNorth(self,speed=None) :
        return self.look( 0, 1, speed )
    
    def lookSouthEast(self, speed=None) :
        return self.look( 1, -1, speed )
    
    def lookSouthWest(self, speed=None) :
        return self.look( -1,-1 )
    
    def lookNorthWest(self, speed=None) :
        return self.look( -1, 1, speed )
    
    def lookNorthEast(self, speed=None) :
        return self.look( 1, 1, speed )
    
    def move( self, dx, dy,speed=None) :
    
        if self.isMoving() :
            print( "Already moving. Ignored")
        else :
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
    

    def canShove( self, pusher, dx, dy, speed, force ) :
        # Default is to be immovable.
        return False

    def shove( self, pusher, dx, dy, speed ) :
        self.move( dx, dy, speed )

    def isEmpty( self ) :
        return False
    
    def isMoving( self ) :
        return (self.dx != 0) or (self.dy != 0)

    def explode( self ) :
    
        Explosion(self.actor) \
            .projectiles(10) \
            .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5) \
            .pose("fragment") \
            .createActor() \

        self.actor.kill()


    
    # Called after the object has finished travelling from one square to another.
    # dx,dy : The direction the invader was travelling ) {
    def onArrived( self, dx, dy ) :
        # Do nothing.
        pass
           
    # Called when a GridRole is halfway through encrouching into my teritory.
    def onInvaded( self, invader ) :
        # Do nothing    
        pass
        
    # Tidy up when dead. Make sure that the square I'm occupying and the square I'm entring no longer
    # reference me.
    def onDeath( self ) :
        if self.isMoving() :
            square = self.findLocalSquare( self.dx, self.dy )
            if square and (square.entrant == self) :
                square.entrant = None
                
        if self.square :
            self.square.occupant = None

        self.actor.kill()
        
    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


def getDirectionAbreviation( dx, dy ) :
    return ["W", "", "E" ][dx + 1] + ["S", "", "N"][dy + 1]



    

