Class.js
========

Implements a traditional Object Oriented class heirarchy.

Example
-------

Craft = Class( {

    Class: {
        exampleClassMethod: function() {
            // whatever
        }
    },

    init: function( speed ) {
        // Treat this like a constructor - It is called just after the object is created, with the
        // arguments passed. e.g. : new Craft(10); will call init with an arguments of 10.
        
        // initialise some instance variables :
        this.speed = speed;
        this.distance = 0;
    },
    
    move: function() {
        this.distance += speed;
    }
});

// We can define static variables like so :
Craft.blah = xyz;
// Note that this is NOT a class variable as Ship.blah will return 'undefined'.

Ship = Class({

    // We are extending the the Craft class.
    Extends: Craft,
    
    init: function( speed ) {
        // It is up to you to call the base class's constructor. You don't have to and it does need to be first.
        Super(speed);
        this.distance = 10;
    },
    
    move: function() {
        Super(); // Calls the parent class's "move" method - in this example with no arguments.
        // Note, we can NOT do anything like : Super.otherMethod( xyz );
        
        // Now we can add some extra behaviour here.
    }

});

// Creating objects
var myCraft = new Craft( 3 );
var myShip = new Ship( 5 );

// Calling instance methods
myShip.move();

// How to call class methods (similar to, but better than, java's static methods) :
Craft.exampleClassMethod();

// Will both call Craft.exampleClassMethod.
// Class methods are inherited from one class to another, and can be overriden.
// This gives more flexibiliity than Java's static methods.
myCraft.Class.exampleClassMethod();
myShip.Class.exampleClassMethod();

// We can also do :
Ship.Class.exampleClassMethod();




