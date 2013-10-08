function Class( attributes )
{
    // The Class object that's to be created.
    var result;
    
    // This will be run when "new" is called, and will call the special "init" method if it has one.
    result = function() {
        // Constructor
        this.Class = result;
        if (this.init) {
            this.init.apply(this,arguments);
        }
    };
    
    // Each class has a special "Parent" attribute, which will lead up the chain to "Object".
    // Object itself doesn not have a "Parent" attribute.
    if ( attributes.Extends ) {
        result.Parent = attributes.Extends;
    } else {
        result.Parent = Object;
    }

    // Create a hash for the class methods    
    result.__proto__ = {};
    
    // Instance methods will inherit the parent class's instance methods
    result.prototype.__proto__ = result.Parent.prototype;
    // Class methods will inherit the  base class's class methods
    result.__proto__.__proto__ = result.Parent.__proto__;
    
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
                result.__proto__[classItem] = classValue;
            }
        
        } else {
        
            // Add the instance method to the class's prototype.
            if (typeof(value) == 'function') {
                result.prototype[item] = value;
                if ((superValue) && (value !== superValue)) {
                    result.prototype[item] = Class.createOverride( result, item );
                }
            } else {
                throw "Unexpected non-function attribute : " + item;
            }
        }
    }
    
    return result;
}

// A global used by Class.creatOverride, and any methods which want to call their parent class's implementation.
var Super = undefined;

// Wraps an instance method in a closure, so that it can call "Super", which will be the parent class's
// implementation of that method.
Class.createOverride = function( klass, name )
{
    var superMethod = klass.prototype.__proto__[name];
    var thisMethod = klass.prototype[name];
    
    var func = function() {
        var me = this;
        try {
            Super = function() { superMethod.apply( me, arguments ); };
            return thisMethod.apply( this, arguments );
        } finally {
            Super = undefined;
        }
    }
    // Makes dumping objects more readable, by hiding this wrapper.
    func.toString = function() { return "" + thisMethod; }; 
    func.__noDump__ = true;
    
    return func;
}

