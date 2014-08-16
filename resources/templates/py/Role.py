from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

# TODO If your Role uses properties, you'll need to import on or more of these...
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

class ${Name}(AbstractRole) :

    def __init__(self) :
        # TODO Initialise your object. Note you can't access self.getActor() yet. e.g. :
        # self.myString = "Default Value"
        # self.myInteger = 0
        pass
        
    def onBirth(self):
        # TODO Called soon after the actor is created and after it has been placed on a Stage.
        pass

    def tick(self):
        # TODO Called 60 times a second, and is where all the good stuff belongs!
        pass


    # TODO Other methods include :
    # onSceneCreated, onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


