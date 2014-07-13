function Class( attributes )
{
    // The Class object that's to be created.
    var result;
    
    // Each class has a special "Parent" attribute, which will lead up the chain to "Object".
    // Object itself doesn't have a "Parent" attribute.
    var parent = Object;
    if ( attributes.Extends ) {
        parent = attributes.Extends;
    }
    // This will be run when "new" is called, and will call the special "init" method if it has one.
    result = function() {
        // Constructor
        this.Class = result.Class;
        if (this.init) {
            this.init.apply(this,arguments);
        }
    };
    result.Parent = parent;
    
    
    // Instance methods will inherit the parent class's instance methods
    result.prototype = new result.Parent();

    // Create the Class object containing class methods and class variable.
    // The Class object can be accessed using myInstance.Class, or MyConstructorFunction.Class 
    var konstructor = function() {};
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

            // Add class methods to the class object.
            for (classItem in value) {
                var classValue = value[classItem];
                result.Class[classItem] = classValue;
            }

        } else {
        
            // Add the instance method to the class's prototype.
            if (typeof(value) == 'function') {
                // TODO Can this be tidied up, its setting the same thing twice???
                result.prototype[item] = value;
                if ((superValue) && (value !== superValue)) {
                    result.prototype[item] = Class.createOverride( result, item );
                }
            } else {
                // Non function attributes are assumed to be class variables.
                // Instance variables are defined using this.blah = value in the "init" method
                // (or in fact any other method).
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
Object.Class = new function() {};
