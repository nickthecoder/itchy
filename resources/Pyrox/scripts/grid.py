from common import *

from square import Square

class Grid(object) :

    def __init__( self, squareSize, across, down, offsetX, offsetY ) :
    
        # The array of squares which make up self grid.
        # Note that the squares are arranged in the same direction as the pixel coordinates,
        # So square (0,0) is at the bottom left.
        self.squares = []

        self.squareSize = squareSize # The size of a square in pixels. Only squares supported, not rectangles.
        self.across = int(across) # The grid is self number of squares across
        self.down = int(down) # The grid is self number of squares down.
        self.offsetX = offsetX # An offset for the pixel position of the bottom left of the grid.
        self.offsetY = offsetY
        
        from empty import Empty
        from outOfBounds import OutOfBounds
        self.empty = Empty()
        self.outOfBounds = OutOfBounds()
        
        # Create the grid array, filling it with emptyness.
        for i in range(0,across) :
            self.squares.append( [] )
            for j in range(0,down) :
                self.squares[i].append( Square( self, i, j ) )



    
    # Finds the square at the given grid location, or None if it is out of bounds.
    def getSquare( self, x, y ) :
        if ((x<0) or (y <0) or (x>= self.across) or (y>=self.down)) :
            return None
        else :
            return self.squares[x][y]



    # Finds the square at the given pixel coordinates, or None if it is out of bounds.
    def getSquareByPixel( self, px, py ) :
        return self.getSquare(self.toGridX(px), self.toGridY(py))

    
    # Returns the GridRole at the given point. Will NOT return None.
    # If x,y are out of bounds, then a special OutOfBounds object is returned.
    # If x,y is empty, then a special Empty object is returned.
    def getGridRole( self, x, y ) :
        square = self.getSquare(x,y)
        if (square is None) :
            return self.outOfBounds

        if square.occupant is None :
            if square.alternateOccupant is None :
                return self.empty
            else :
                return square.alternateOccupant
            
        return square.occupant

    
    # converts a grid X to a pixel X
    def toPixelX( self, x ) :
        return x * self.squareSize + self.offsetX

    
    # converts a grid Y to a pixel Y
    def toPixelY( self, y ) :
        return y * self.squareSize + self.offsetY

    
    # converts a pixel X to a grid X
    def toGridX( self, px ) :
        return int(math.floor( (px - self.offsetX) / self.squareSize ))


    # converts a pixel Y to a grid Y
    def toGridY( self, py ) :
        return int(math.floor( (py - self.offsetY) / self.squareSize ))

    def getEmpty(self) :
        return self.empty;
        



