
GridRole = Class({
    Extends: RoleScript,

    roleName: "GridRole",
        
    init: function() {
        // TODO Initialise your object. Note you can't access this.actor yet.
        this.speed = directorScript.squareSize; // Number of pixels to move per tick when dx or dy are non zero.
        this.dx = 0; // -1, 0 or 1 depending on direction of travel
        this.dy = 0; // -1, 0 or 1 depending on direction of travel.
        this.between = 0; // Distance travelled between squares (0..squareSize)
        this.squashed = false; // Set to false when started to move and set to true just before onInvaded is called.
        // this.square = null; // The square I currently occupy.
    },

    tick: function() {

        if ( (this.dx != 0) || (this.dy != 0) ) {

            this.between += this.speed;

            if ( ! this.squashed ) {
                if (this.between >= this.square.grid.squareSize / 2) {
                    this.squashed = true;
                    var squashing = this.findLocalRole( this.dx, this.dy );
                    squashing.onInvaded( this );
                }
            }
                        
            this.actor.moveBy( this.dx * this.speed, this.dy * this.speed );

            if (this.between >= this.square.grid.squareSize) {
                // Correct for any overshoot
                var overshoot = this.between - this.square.grid.squareSize;
                this.actor.moveBy( this.dx * - overshoot, this.dy * - overshoot );
                
                // Stop the movement and occupy the new square.
                var dx = this.dx; // Remember the direction I was travelling as its needed after dx,dy are reset.
                var dy = this.dy;
                this.between = 0;
                this.dx = 0;
                this.dy = 0;
                this.occupySquare( this.findLocalSquare( dx, dy ) );
                this.onArrived(dx, dy);
            }
        }  
    },
    
    // Tidy up when dead. Make sure that the square I'm occupying and the square I'm entring no longer
    // reference me.
    onDeath: function() {
        if ( this.isMoving() ) {
            var sqaure = this.findLocalSquare( this.dx, this.dy );
            if ( square && square.entrant == this ) {
                square.entrant = null;
            }
        }
        if ( this.square ) {
            this.square.occupant = EmptyGridRole.instance;
        }
        this.actor.kill();
    },
    
    occupySquare: function( newSquare ) {
        // It is possible for another GridRole to have occupied my old square before I have completely vacated it,
        // in which case, do not interfer. Otherwise, reset the occupant to the global "Empty" object.
        if (this.square.occupant == this) {
            this.square.occupant = EmptyGridRole.instance;
        }
        
        if (newSquare == null) {
            this.actor.kill();
        } else {
            newSquare.occupant = this;
            newSquare.entrant = null;
        }
        
        this.square = newSquare;
    },
    
    
    // Boiler plate code - no need to alter it.
    getProperties: function() {
        return GridRole.properties;
    },
    
    // Called by Level.js just after a level has been loaded, and must also be called manually if you create
    // new actors on the grid dynamically.
    // Uses the actors position to calculate the correct square to occupy.
    placeOnGrid: function( grid ) {
        this.square = grid.getSquareByPixel( this.actor.getX(), this.actor.getY() );
        if (this.square != null) {
            this.square.occupant = this;
        } else {
            stdout.println( "Failed to find square! " + this.actor.getX() + "," + this.actor.getY() );
            this.actor.getAppearance().setAlpha( 128 );
        }
    },
    
    // Finds the square
    findLocalSquare: function( dx, dy ) {
        var x = this.square.x + dx;
        var y = this.square.y + dy;
        return this.square.grid.getSquare(x, y);
    },
    
    // Finds the square
    findLocalRole: function( dx, dy ) {
        var x = this.square.x + dx;
        var y = this.square.y + dy;
        return this.square.grid.getGridRole(x, y);
    },
    
    // Finds the GridRole in the given direction. If this lands us outside the whole grid, then a global "edge"
    // is returned, which is solid and unchanging. If there is nothing at that position, then a global "empty"
    // is returned.
    // When objects are moving between squares, the result is a little more complex.
    // If there is an entrant, then return that, not the occupier (which will move out, or be squashed).
    // However, if the occupant is leaving, and will be gone by the time I could enter, then don't return the occupant, 
    // and instead the global EmptyGridRole.instance.
    look: function( dx, dy, speed ) {
        this.debug( "Look " + dx + "," + dy );
        if (!speed) {
            speed = this.speed;
        }
        
        var square = this.findLocalSquare(dx, dy);
        // If there is an object entering, then this is the one we care about.
        var entrant = (square == null) ? null : square.entrant;
        this.debug( "Square : " + square + " entrant : " + entrant );
        if (entrant != null) {
            this.debug( "Using entrant because there is one" );
            return entrant;
        }
        
        var occupier = this.findLocalRole( dx, dy );
        if ( ! occupier.isMoving() ) {
            this.debug( "Using occupier because not moving" );
            return occupier;
        }
        
        // Calculate how long (in frames) it will take for the occupant to leave the square, and
        // for me to enter the square.
        var willLeaveTicks = (this.square.grid.squareSize - occupier.between) / occupier.speed;
        var willEnterTicks = this.square.grid.squareSize / speed;

        // this.debug( "Will leave : " + willLeaveTicks + " vs will enter : " + willEnterTicks );

        // If it will leave before I get half way, then ignore it.
        if (willLeaveTicks < willEnterTicks ) {
            this.debug( "Using EMPTY because the occupier will move out" );
            return EmptyGridRole.instance;
        }
        this.debug( "Using occupier because it isn't moving fast enough to ignore" );
        return occupier;
    },
    
    debug: function( message ) {
        return;
        if ( (this.square.x == 1) && (this.square.y == 1) ) {
            stdout.println( message );
        }
    },
    
    lookEast: function(speed) {
        return this.look( 1, 0, speed );
    },
    
    lookSouth: function(speed) {
        return this.look( 0,-1, speed );
    },
    
    lookWest: function(speed) {
        return this.look( -1, 0, speed );
    },
    
    lookNorth: function(speed) {
        return this.look( 0, 1, speed );
    },
    
    lookSouthEast: function(speed) {
        return this.look( 1, -1, speed );
    },
    
    lookSouthWest: function(speed) {
        return this.look( -1,-1 );
    },
    
    lookNorthWest: function(speed) {
        return this.look( -1, 1, speed );
    },
    
    lookNorthEast: function(speed) {
        return this.look( 1, 1, speed );
    },
    
    

    move: function(dx, dy) {
        if (this.isMoving()) {
            out.println( "Already moving. Ignored");
        } else {
            this.squashed = false;
            this.dx = dx;
            this.dy = dy;
            this.between = 0;
            this.findLocalSquare( dx, dy ).entrant = this;
        }
    },
    
    moveEast: function() {
        this.move( 1, 0 );
    },
    
    moveSouth: function() {
        this.move( 0, -1);
    },
    
    moveWest: function() {
        this.move( -1, 0 );
    },
    
    moveNorth: function() {
        this.move( 0, 1 );
    },
    

    pushed: function(pusher, dx, dy, force) {
        // Default is to be immovable.
        return false;
    },

    isEmpty: function() {
        return false;
    },
    
    isMoving: function() {
        return (this.dx != 0) || (this.dy != 0);
    },
    
    
    // Called after the object has finished travelling from one square to another.
    // dx,dy : The direction the invader was travelling ) {
    onArrived: function(invader, dx, dy ) { 
        // Do nothing.
    },
    
    // Called when a GridRole is halfway through encrouching into my teritory.
    onInvaded: function( invader ) {
        // Do nothing
    }
    
});

import("Grid.js");
import("EmptyGridRole.js");

GridRole.properties = new java.util.ArrayList();
// TODO Define each editable property like so :
// GridRole.properties.add( new itchy.property.StringProperty("myString").label("My String") );
// GridRole.properties.add( new itchy.property.IntegerProperty("myInteger").label("My Integer") );
// GridRole.properties.add( new itchy.property.DoubleProperty("myDouble").label("My Double") );
// GridRole.properties.add( new itchy.property.BooleanProperty("myBoolean").label("My Boolean") );
// GridRole.properties.add( new itchy.property.RGBAProperty("myColor").label("My Colour") );
// Each property should be initialised to a default value in the "init" method.

