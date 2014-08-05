from uk.co.nickthecoder.itchy import Itchy

from movable import Movable
from part import Part

# A Movable GridRole, which takes up more than one Square. This object is the main occupant of one square,
# and there are companion "Part" roles, which occupy the other squares. When looking outwards, many
# sqaures need to be considered, so rather than code each possibility, there are special methods
# which scan all the needed neighbours.
#
# Subclass Big, and call createPart for each additional square required, then call calculateNeighbours.
# These should both be done after the parent object has been placed on the grid (from onPlaceOnGrid
# most likely).
#
class Big(Movable) :

    def __init__(self) :
        Movable.__init__(self)
        
        # A list of Part objects
        self.parts = []

    # This is a factor method. Ofter, a normal Part is good enough, but you may find that you need
    # something special, in which case, override this method, and create custom Parts.
    # These should be subclasses of Part, or be compatable with Part.
    def createPart(self, dx, dy) :
        return Part( self, dx, dy )

    def calculateLeadingEdges(self) :
        # This is a 3d array. The first two are the direction of travel (dx, dy), the third is the 
        # set of Part objects.
        self.leadingEdges = []
        
        for dx in range(-1,2) :
            xset = []
            for dy in range(-1,2) :
                xset.append( self.findLeadingEdges(dx, dy) )
            self.leadingEdges.append( xset )


    # Looks in a given direction from myself, and all of my parts, and if the neighbour is not
    # part of me, then that must be a leading edge.
    # For example, a 2x1 object would have two leading edges in the north direction, and just one east,
    # whereas a 2x2 object would have two leading edges in each direction.
    def findLeadingEdges( self, dx, dy ) :
        result = []

        neighbour = self.look( dx, dy )
        if not self.isPartOfMe( neighbour ) :
            result.append(self)

        for part in self.parts :
            neighbour = part.look( dx, dy )
            if not self.isPartOfMe( neighbour ) :
                result.append(part)
                
        return result

    # Return True if this neighbour, me (self) or one of my Parts?
    # Return False if it is neither.
    def isPartOfMe(self, neighbour) :
        if neighbour is self :
            return True
        if isinstance(neighbour,Part) and neighbour.parent is self :    
            return True
        return False
        
    # Returns an array of Part objects which are on the leading edge when moving in the specified direction.
    def getLeadingEdges( self, dx, dy ) :
        return self.leadingEdges[dx + 1][dy +1]

    def allAddTag( self, tag ) :
        for part in self.parts :
            part.addTag( tag )
        self.addTag( tag )
    
    def allRemoveTag( self, tag ) :
        for part in self.parts :
            part.removeTag( tag )
        self.removeTag( tag )
    
    # Return true if any of my neighbours (in the given direction) are moving.
    def neighbourIsMoving( self, dx, dy ) :
        for edge in self.getLeadingEdges(dx, dy) :
            neighbour = edge.look( dx, dy )
            if neighbour.isMoving() :
                return True
        return False

    # Return true if ALL of my neighbours (in the given direction) have the tag.
    def neighboursHaveTag( self, tag, dx, dy ) :
        for edge in self.getLeadingEdges(dx, dy) :
            neighbour = edge.look( dx, dy )
            if not neighbour.hasTag(tag) :
                return False
        return True

    # Return true if ALL of my neighbours (in the given direction) have one of the tags.
    def neighboursHaveTags( self, tags, dx, dy ) :
        for edge in self.getLeadingEdges(dx, dy) :
            neighbour = edge.look( dx, dy )
            hasTag = False
            for tag in tags :        
                if neighbour.hasTag(tag) :
                    hasTag = True
            if not hasTag :
                return False
                
        return True

    
    def canShoveNeigbours( self, dx, dy, speed, force, squashTags=None ) :
        # We ask ALL leading edges if they can be shoved, rather than exiting the loop early if
        # one cannot, because canShove can have side effects - like pushing a button. See class "Hard"
        result = True
        for edge in self.getLeadingEdges(dx, dy) :
            neighbour = edge.look( dx, dy )
            if not self.hasAnyTag( neighbour, squashTags ) :
                if not neighbour.canShove(self,dx, dy, speed, force) :
                    result = False

        return result

    def shoveNeighbours( self, dx, dy, speed=None, squashTags=None ) :
        if speed is None :
            speed = self.speed
            
        for edge in self.getLeadingEdges(dx, dy) :
            neighbour = edge.look( dx, dy )
            if not self.hasAnyTag( neighbour, squashTags ) :
                neighbour.shove( self, dx, dy, speed )
        
    def move( self, dx, dy, speed=None ) :
    
        Movable.move( self, dx, dy, speed )

        for part in self.parts :
            part.move( dx, dy, speed )


    def hasAnyTag( self, neighbour, tags ) :
        if tags is None :
            return False
            
        for tag in tags :
            if neighbour.hasTag( tag ) :
                return True
        return False


    def tick(self) :

        Movable.tick(self)

        for part in self.parts :
            if part.square :
                part.tick()

    def removeFromGrid(self) :
        Movable.removeFromGrid(self)
        for part in self.parts :
            part.removeFromGrid()
            
    def onExplode(self) :
        for part in self.parts :
            part.explode()
        self.explode()

