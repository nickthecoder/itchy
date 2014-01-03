from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

# TODO If your SceneDirector has properties, you'll need to import one or more of these...
# from uk.co.nickthecoder.itchy.property import StringProperty
# from uk.co.nickthecoder.itchy.property import IntegerProperty
# from uk.co.nickthecoder.itchy.property import BooleanProperty
# from uk.co.nickthecoder.itchy.property import DoubleProperty
# from uk.co.nickthecoder.itchy.property import RGBAProperty

properties = ArrayList()
# TODO declare properties here. Note that you must also initialise them in __init__
# properties.add( StringProperty( "My String", "myString" ) )
# properties.add( IntegerProperty( "My Integer", "myInteger" ) )
# properties.add( DoubleProperty( "My Double", "myDouble" ) )
# properties.add( BooleanProperty( "My Boolean", "myBoolean" ) )
# properties.add( RGBAProperty( "My Colour", "myColor", false, false ) )
# properties.add( RGBAProperty( "My Transparent Colour", "myOtherColor", false, true ) )

class ${Name}(PlainSceneDirector) :

    def __init__(self) :
        # TODO Initialise your object.
        # self.myString = "Default Value"
        # self.myInteger = 0
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


