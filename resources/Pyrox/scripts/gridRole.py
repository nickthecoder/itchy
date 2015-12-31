from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Fragment
from uk.co.nickthecoder.itchy.role import ExplosionBuilder
from uk.co.nickthecoder.itchy.role import TalkBuilder

from java.util import ArrayList

from grid import Grid

properties = ArrayList()

class GridRole(AbstractRole) :

    def __init__(self) :
        self.square = None
        self.role = self # So that the look method can return a GridRole or a Comound, and both have a "role" attribute.
        self.alternateRole = None # Same reason as self.role. This should always be None
        
        self.speed = 4 # The default speed for this object.
        
        # Each GridRole object knows when its tick was last called. Used by GridStage to ensure it
        # doesn't tick a role more than once per frame.
        self.latestTick = -1
        
        self.idle = True
        self.talkActor = None

        self.talkX = 70
        self.talkY = 10        


    def onBirth( self ) :
        Fragment().actor(self.actor).pieces( 10 ).createPoses( "fragment" )
        self.addTag( "explodable" )
    
    def onAttach( self ) :
        pass
        
    def talk( self, message ) :
        if self.talkActor :
            self.talkActor.kill()

        if message.startswith( "_"  ) :
            message = self.actor.costume.getString( message[1:], message[1:] )
        else :
            message = message.replace("\\n","\n")

        talk = TalkBuilder(self.actor) \
            .style("talk-style") \
            .offset(self.talkX, self.talkY).alignment( 1, 1 ) \
            .text(message)
        self.adjustTalk( talk );
        
        self.talkActor = talk.create().getActor()
        
        self.talkActor.getStage().addTop(self.talkActor)
        self.talkActor.setCostume( self.getActor().getCostume() )
        self.talkActor.event("talk-fade")
               
    def adjustTalk( self, talkBuilder ) :
        pass;

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
            self.onPlacedOnGrid()
        else :
            print "Failed to find square! ", self.actor.getX(), ",", self.actor.getY()
            self.getActor().getAppearance().setAlpha( 128 )

    def onPlacedOnGrid( self ) :
        pass
    
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
    # In addition, each square has an alternateOccupant, and this is returned, when otherwise the result would have
    # been Empty.
    # One final, and tricky complication, a square can have an occupant and an alternateOccupant. If both are found,
    # then instead of a GridRole and Compound is returned. Therefore you cannot assume the result from look is a
    # role, if you want the role, add ".role". e.g. : foo.look( 1,0 ).role.onExplode()
    def look( self, dx, dy, speed=None ) :

        if speed is None :
            speed = self.speed
        
        square = self.findLocalSquare(dx, dy)
        # If there is an object entering, then this is the one we care about.
        entrant = None if (square is None) else square.entrant
        if entrant :
            return entrant
        
        if square and square.alternateOccupant :
            return square.getOccupant()
             
            
        occupier = self.findLocalRole( dx, dy )
        if not occupier.isMoving() :
            return occupier

        # Calculate how long (in frames) it will take for the occupant to leave the square, and
        # for me to enter the square.
        willLeaveTicks = (self.square.grid.squareSize - occupier.between) / occupier.speed
        willEnterTicks = self.square.grid.squareSize / speed

        isOpposite = dx == -occupier.dx and dy == -occupier.dy
        
        # If it will leave before I get half way, then ignore it.
        if willLeaveTicks < willEnterTicks and not isOpposite :
            if square.alternateOccupant :
                return square.alternateOccupant
            else :
                return square.grid.getEmpty()

        return occupier
        
    def lookDirection(self, direction, speed=None) :

        return self.look( self.getDeltaX(direction), self.getDeltaY(direction), speed )


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

    def shove( self, pusher, dx, dy, speed ) :
        pass
        
    def isEmpty( self ) :
        return False
    
    def isMoving( self ) :
        return False

    def onExplode( self ) :
        self.explode()

    def explode( self ) :        
    
        ExplosionBuilder(self.actor) \
            .projectiles(10) \
            .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5) \
            .pose("fragment") \
            .create()

        self.actor.deathEvent("explode")


    # Called when a GridRole is halfway through encrouching into my teritory.
    def onHalfInvaded( self, invader ) :
        pass
                
    # Called when a GridRole is just about to take over my square.
    # The only way to prevent me being booted out is to override this method, and remove the invader
    # from the grid instead.
    def onInvaded( self, invader ) :
        self.removeFromGrid()
        
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
            if self.square.occupant == self :
                self.square.occupant = None
            elif self.square.alternateOccupant == self :
                self.square.alternateOccupant = None

        self.square = None
        self.dx = 0
        self.dy = 0

    # Each square has an alternative occupant, so that two roles can share the same square.
    # This methods downgrades a role from being the normal occupant to the alternate occupant.
    # Must NOT be used for movable objects
    def makeAlternateOccupant( self ) :
    
        if self.square.occupant != self :
            return
        self.square.occupant = None

        if self.square.alternateOccupant :
            self.square.alternateOccupant.removeFromGrid()        
        
        self.square.alternateOccupant = self;
    
    def unmakeAlternateOccupant( self ) :
        if self.square.alternateOccupant != self :
            print "Not an alternateOccupant!"
            return
        if self.square.occupant != None :
            print "Not empty"
            return
        self.square.alternateOccupant = None
        self.square.occupant = self


    @staticmethod
    def getDirectionAbbreviation( direction ) :
        return [ "E","N","W","S" ][ direction ]

    @staticmethod
    def getCompassAbbreviation( dx, dy ) :
        return ["W", "", "E" ][dx + 1] + ["S", "", "N"][dy + 1]

    @staticmethod
    def getDeltaX( direction ) :
        return [1,0,-1,0][direction]

    @staticmethod
    def getDeltaY( direction ) :
        return [0,1,0,-1][direction]


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


    def toString(self) :
        return self.__str__()
        
    def __str__(self) :
        return self.__class__.__name__ + " @ " + str(self.square.x) + "," + str(self.square.y)

    

