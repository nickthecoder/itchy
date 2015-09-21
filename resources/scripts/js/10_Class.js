/*

Usage :
To create a class definition :

MyClass = Class({
    init: function() {
        // Called as part of : new MyClass();
    },
    
    method1: function() {
    },
    
    method2etc: function() {
    }
});

To define a sub-class :

MySubClass = Class({
    Extends: MyClass,
    
    init: function() {
        Super(); // Calls the base class's init method.
        // More initialisation
    },
    
    method1: function() {
        Super(); // Calls the base class's method1.
        // added functionality
    }
});

To create class methods, and class variables :

Foo = Class({
    
    Class: {
        title: "Example class variable",
        
        cm: function() {
            // An example class method
            var t = "Wow " + this.title;
            // Note that "this" is the Class Foo, not an instance of Foo. 
        }
    },

    bar: function() {
        var t1 = this.Class.title;
        // Or alternately
        var t2 = Foo.Class.title;
        
        this.Class.cm();
        Foo.Class.cm();
    }
});

*/
  

function Class( attributes )
{    
    // This is the constructor function that will be run when "new" is called.
    // It calls the special "init" method if there is one defined.
    var result = function() {
        // Constructor
        this.Class = result.Class;
        if (this.init) {
            this.init.apply(this,arguments);
        }
    };
    
    // Each class has a special "Parent" attribute, which will lead up the chain of inherrited constructors.
    // till it gets to "Object".
    result.Parent = attributes.Extends ? attributes.Extends : Object;    

    // Instance methods will inherit the parent class's instance methods
    result.prototype = new result.Parent();

    // Create the Class object containing class methods and class variable.
    // The Class object can be accessed using myInstance.Class, or MyConstructorFunction.Class
    var konstructor = function() {};
    // The class methods are inherited from the base classes class methods. 
    konstructor.prototype = result.Parent.Class;
    result.Class = new konstructor();
    result.Class.Parent = result.Parent.Class;


    // Loop over all attributes passed, (the instance methods and the special values 'Extends' and 'Class')
    for (item in attributes ) {
        var value = attributes[item];
        var superValue = result.Parent.prototype[item];
        
        if (item == 'Extends') {
            // This attribute has already been dealt with.
            
        } else if (item == 'Class') {

            // Add class methods and class variables to the class object.
            for (classItem in value) {
                var classValue = value[classItem];
                result.Class[classItem] = classValue;
            }

        } else {
        
            // Add the instance method to the class's prototype.
            if (typeof(value) == 'function') {
                result.prototype[item] = value;
                if ((superValue) && (value !== superValue)) {
                    // Note that the previously set value to result.prototype[item] is used within the
                    // createOverride method, so BOTH assignments are needed.
                    result.prototype[item] = Class.createOverride( result, item );
                }
            } else {
                // Non function attributes are assumed to be class variables.
                result.Class[item] = value;
            }
        }
    }
    
    return result;
}

var Super = undefined;

// Wraps an instance method in a closure, so that it can call "Super", which will be the parent class's
// implementation of that method.
Class.createOverride = function( klass, name )
{
    var superMethod = klass.Parent.prototype[name];
    var thisMethod = klass.prototype[name];
    
    var func = function() {
        var me = this;
        var oldSuper = Super;
        try {
            Super = function() { superMethod.apply( me, arguments ); };
            return thisMethod.apply( this, arguments );
        } finally {
            Super = oldSuper;
        }
    }
    // Makes dumping objects more readable, by hiding this wrapper.
    func.toString = function() { return "" + thisMethod; }; 
    func.__noDump__ = true;
    
    return func;
}

// Create an empty Class definition for the topmost Class in the hierarchy.
Object.Class = new Object();
