from common import *

properties = ArrayList()
# TODO declare poroperties here. Note that you must also initialise them in __init__
# properties.add( StringProperty( "myString" ) )
# properties.add( IntegerProperty( "myInteger" ) )
# properties.add( DoubleProperty( "myDouble" ) )
# properties.add( BooleanProperty( "myBoolean" ) )
# properties.add( RGBAProperty( "myColor" ).label( "My Colour" ) )
# properties.add( RGBAProperty( "myOtherColor" ).allowNull().includeAlpha() )

class ${Name}(PlainSceneDirector) :

    def __init__(self) :
        # TODO Initialise your object.
        pass
    
    def tick(self) :
        # TODO Called 60 times a second
        pass


    # TODO Other methods include :
    # onActivate, onDeactivate(), onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


