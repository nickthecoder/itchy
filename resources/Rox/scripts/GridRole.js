import("Grid.js");

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
                    var squashing = this.look( this.dx, this.dy );
                    stdout.println("Squashing : " + squashing.Class.roleName );
                    squashing.onInvaded( this );
                }
            }
                        
            if (this.between >= this.square.grid.squareSize) {
                // Correct for any overshoot
                var overshoot = this.between - this.square.grid.squareSize;
                this.actor.moveBy( this.dx * overshoot, this.dy * overshoot );
                
                // Stop the movement and occupy the new square.
                var dx = this.dx; // Remember the direction I was travelling as its needed after dx,dy are reset.
                var dy = this.dy;
                this.between = 0;
                this.dx = 0;
                this.dy = 0;
                this.occupySquare( this.findLocalSquare( dx, dy ) );
                this.onArrived(dx, dy);
            } else {
                this.actor.moveBy( this.dx * this.speed, this.dy * this.speed );
            }
        }  
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
    
    // Finds the GridRole in the given direction. If this lands us outside the whole grid, then a global "edge"
    // is returned, which is solid and unchanging. If there is nothing at that position, then a global "empty"
    // is returned.
    // When objects are moving between squares, the result is a little more complex.
    // If the occupant is leaving, and will be gone by the time I could enter, then don't return the occupant, 
    // and instead return the entrant. If there is no entrant, then use the global EmptyGridRole.instance.
    look: function( dx, dy ) {   
        var occupier = this.square.grid.getGridRole( this.square.x + dx, this.square.y + dy );
        if ( ! occupier.isMoving() ) {
            return occupier;
        }
        
        // Calculate how long (in frames) it will take for the occupant to leave the square, and
        // for me to enter the square.
        var willLeaveTicks = (this.square.grid.squareSize - occupier.between) / occupier.speed;
        var willEnterTicks = this.square.grid.squareSize / this.speed;

        // If it will leave before I get half way, then ignore it.
        if (willLeaveTicks > willEnterTicks/2) {
            var entrant = this.square.grid.getSquare(x,y).entrant;
            if (entrant == null) {
                return EmptyGridRole.instance;
            } else {
                return entrant;
            }
        }
        return occupier;
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
    
    move: function(dx, dy) {
        if (this.isMoving()) {
            out.println( "Already moving. Ignored");
        } else {
            this.squashed = false;
            this.dx = dx;
            this.dy = dy;
            this.between = 0;
        }
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

import("EmptyGridRole.js");

GridRole.properties = new java.util.ArrayList();
// TODO Define each editable property like so :
// GridRole.properties.add( new itchy.property.StringProperty("myString").label("My String") );
// GridRole.properties.add( new itchy.property.IntegerProperty("myInteger").label("My Integer") );
// GridRole.properties.add( new itchy.property.DoubleProperty("myDouble").label("My Double") );
// GridRole.properties.add( new itchy.property.BooleanProperty("myBoolean").label("My Boolean") );
// GridRole.properties.add( new itchy.property.RGBAProperty("myColor").label("My Colour") );
// Each property should be initialised to a default value in the "init" method.

