import( "EmptyGridRole.js" );

Square = Class({

    init: function( grid, x, y ) {
        this.grid = grid; // Every square knows its parent grid.
        this.occupant = EmptyGridRole.instance; // The default occupant is emptyness - a SHARED GridRole!
        this.entrant = null; // The GridRole that is entering this square - only one GridRole can be entering.
        this.x = x;
        this.y = y;
    }

});

