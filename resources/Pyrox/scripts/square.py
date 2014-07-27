class Square(object) :

    def __init__( self, grid, x, y ) :
        import empty;
        self.grid = grid # Every square knows its parent grid.
        self.occupant = None # The default occupant is nothing
        self.alternateOccupant = None
        self.entrant = None # The GridRole that is entering this square - only one GridRole can be entering.
        self.x = x
        self.y = y


