from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

# TODO You'll need to import one or more of these...
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
        
class ${Name}(CostumeProperties) :

    def __init__(self) :
        # TODO Initialise your object.
        # self.myString = "Default Value"
        # self.myInteger = 0
        pass

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


