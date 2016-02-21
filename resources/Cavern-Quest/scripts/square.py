class Square(object) :

    def __init__( self, grid, x, y ) :
        import empty;
        self.grid = grid # Every square knows its parent grid.
        self.occupant = None # The default occupant is nothing
        self.alternateOccupant = None
        self.entrant = None # The GridRole that is entering this square - only one GridRole can be entering.
        self.x = x
        self.y = y

    # Returns either Empty, the occupant, the alternateOccupant, or a compound of the two.
    def getOccupant(self) :
        if self.alternateOccupant :
            if self.occupant is None :
                return self.alternateOccupant
            else :
                return CompoundOccupant( self.occupant, self.alternateOccupant )
        else :
            if self.occupant :
                return self.occupant
            else :
                return self.grid.getEmpty()

            
class CompoundOccupant :

    def __init__( self, a, b ) :
        self.role = a
        self.alternateRole = b

        
    def hasTag( self, tag ) :
        return self.role.hasTag( tag ) and self.alternateRole.hasTag( tag )
        
    def isMoving( self ) :
        return self.role.isMoving() or self.alternateRole.isMoving()
        
    def canShove( self, pusher, dx, dy, speed, force ) :
        return self.role.canShove( pusher, dx, dy, speed, force ) and self.alternateRole.canShove( pusher, dx, dy, speed, force )

    def shove( self, pusher, dx, dy, speed ) :
        self.role.shove( pusher, dx, dy, speed )
        self.alternateRole.shove( pusher, dx, dy, speed )
        
    def isEmpty( self ) :
        return False


