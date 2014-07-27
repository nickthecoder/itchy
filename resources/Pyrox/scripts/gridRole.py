from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Fragment
from uk.co.nickthecoder.itchy.role import Explosion

from java.util import ArrayList

from grid import Grid

properties = ArrayList()

class GridRole(AbstractRole) :

    def __init__(self) :
        self.square = None
        self.speed = 4 # The default speed for this object.
        
        # Used by moving objects. When they move right or down, this is set to true, which prevents the gridStage
        # from calling their tick method twice in one game tick. (Once for their old grid position, and once for
        # their new position).
        # It is reset by gridStage.
        self.movedForward = False
        
    def onBirth( self ) :
        Fragment().actor(self.actor).pieces( 10 ).createPoses( "fragment" )
        self.addTag( "explodable" )
    
    def onAttach( self ) :
        pass
        
        
    def moveTo( self, x, y ) :
   
        grid = self.square.grid if self.square else None
            
        self.removeFromGrid()
        self.getActor().moveTo(x, y)
        if grid :
            self.placeOnGrid(grid)

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
    def look( self, dx, dy, speed=None, debug=False ) :

        if speed is None :
            speed = self.speed
        
        square = self.findLocalSquare(dx, dy)
        # If there is an object entering, then this is the one we care about.
        entrant = None if (square is None) else square.entrant
        if entrant :
            if debug :
                print "Entrant"
            return entrant
        
        occupier = self.findLocalRole( dx, dy )
        if not occupier.isMoving() :
            if debug :
                print "Stationary occupant"
            return occupier
        
        # Calculate how long (in frames) it will take for the occupant to leave the square, and
        # for me to enter the square.
        willLeaveTicks = (self.square.grid.squareSize - occupier.between) / occupier.speed
        willEnterTicks = self.square.grid.squareSize / speed

        if debug:
            print "WillLeave ", willLeaveTicks, "WillEnter", willEnterTicks
            
        # If it will leave before I get half way, then ignore it.
        if willLeaveTicks < willEnterTicks :
            if debug :
                print "Empty"
            return self.square.grid.getEmpty()

        if debug :
            print "Occupier moving out"
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
        return self.look( -1,-1, speed )
    
    def lookNorthWest(self, speed=None) :
        return self.look( -1, 1, speed )
    
    def lookNorthEast(self, speed=None) :
        return self.look( 1, 1, speed )
    
    
    def canShove( self, pusher, dx, dy, speed, force ) :
        # Default is to be immovable.
        return False

    def isEmpty( self ) :
        return False
    
    def isMoving( self ) :
        return False

    def explode( self ) :        
    
        Explosion(self.actor) \
            .projectiles(10) \
            .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5) \
            .pose("fragment") \
            .createActor() \

        self.actor.deathEvent("explode")


    # Called when a GridRole is halfway through encrouching into my teritory.
    def onInvaded( self, invader ) :
        # Do nothing    
        pass
        
    # Tidy up when dead. Make sure that the square I'm occupying and the square I'm entring no longer
    # reference me.
    def onDeath( self ) :
        self.removeFromGrid()
    
    def removeFromGrid( self ) :
        if self.isMoving() :
            square = self.findLocalSquare( self.dx, self.dy )
            if square and (square.entrant == self) :
                square.entrant = None
                
        if self.square :
            self.square.occupant = None
            
        self.square = None
        self.dx = 0
        self.dy = 0

        
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



    

