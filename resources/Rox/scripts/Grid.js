import("Square.js");
import("OutOfBounds.js");

Grid = Class({

    init: function( squareSize, across, down, offsetX, offsetY ) {
    
        // The array of squares which make up this grid.
        // Note that the squares are arranged in the same direction as the pixel coordinates,
        // So square (0,0) is at the bottom left.
        this.squares = new Array( across ); 

        this.squareSize = squareSize; // The size of a square in pixels. Only squares supported, not rectangles.
        this.across = across; // The grid is this number of squares across
        this.down = down; // The grid is this number of squares down.
        this.offsetX = offsetX; // An offset for the pixel position of the bottom left of the grid.
        this.offsetY = offsetY;
        
        // Create the grid array, filling it with emptyness.
        var i,j;
        for ( i = 0; i < across; i ++ ) {
            this.squares[i] = new Array( down );
            for ( j = 0; j < down; j ++ ) {
                this.squares[i][j] = new Square( this, i, j )
            }
        }
    },
    
    // Finds the square at the given grid location, or null if it is out of bounds.
    getSquare: function( x, y ) {
        if ((x<0) || (y <0) || (x>= this.across) || (y>=this.down)) {
            return null;
        } else {
            return this.squares[x][y];
        }
    },      

    // Finds the square at the given pixel coordinates, or null if it is out of bounds.
    getSquareByPixel: function( px, py ) {
        return this.getSquare(this.toGridX(px), this.toGridY(py));
    },
    
    getGridRole: function( x, y ) {
        var square = this.getSquare(x,y);
        if (square == null) {
            return OutOfBounds.instance;
        }
        return square.occupant;
    },
    
    // converts a grid X to a pixel X
    toPixelX: function( x ) {
        return x * this.squareSize + this.offsetX;
    },
    
    // converts a grid Y to a pixel Y
    toPixelY: function( y ) {
        return y * this.squareSize + this.offsetY;
    },
    
    // converts a pixel X to a grid X
    toGridX: function( px ) {
        return Math.floor( (px - this.offsetX) / this.squareSize );
    },

    // converts a pixel Y to a grid Y
    toGridY: function( py ) {
        return Math.floor( (py - this.offsetY) / this.squareSize );
    },

});


