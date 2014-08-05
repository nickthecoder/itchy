from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

# TODO You'll need to import one or more of these...
# from uk.co.nickthecoder.itchy.property import StringProperty
# from uk.co.nickthecoder.itchy.property import IntegerProperty
# from uk.co.nickthecoder.itchy.property import BooleanProperty
# from uk.co.nickthecoder.itchy.property import DoubleProperty
# from uk.co.nickthecoder.itchy.property import RGBAProperty

from java.util import ArrayList

properties = ArrayList()
# TODO declare poroperties here. Note that you must also initialise them in __init__
# properties.add( StringProperty( "myString" ) )
# properties.add( IntegerProperty( "myInteger" ) )
# properties.add( DoubleProperty( "myDouble" ) )
# properties.add( BooleanProperty( "myBoolean" ) )
# properties.add( RGBAProperty( "myColor" ).label( "My Colour" ) )
# properties.add( RGBAProperty( "myOtherColor" ).allowNull().includeAlpha() )
  
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


