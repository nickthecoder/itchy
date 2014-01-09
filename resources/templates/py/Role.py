from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

# TODO If your Role uses properties, you'll need to import on or more of these...
# from uk.co.nickthecoder.itchy.property import StringProperty
# from uk.co.nickthecoder.itchy.property import IntegerProperty
# from uk.co.nickthecoder.itchy.property import BooleanProperty
# from uk.co.nickthecoder.itchy.property import DoubleProperty
# from uk.co.nickthecoder.itchy.property import RGBAProperty

properties = ArrayList()
# TODO declare poroperties here. Note that you must also initialise them in __init__
# properties.add( StringProperty( "My String", "myString" ) )
# properties.add( IntegerProperty( "My Integer", "myInteger" ) )
# properties.add( DoubleProperty( "My Double", "myDouble" ) )
# properties.add( BooleanProperty( "My Boolean", "myBoolean" ) )
# properties.add( RGBAProperty( "My Colour", "myColor", false, false ) )
# properties.add( RGBAProperty( "My Transparent Colour", "myOtherColor", false, true ) )

class ${Name}(AbstractRole) :

    def __init__(self) :
        # TODO Initialise your object. Note you can't access self.getActor() yet. e.g. :
        # self.myString = "Default Value"
        # self.myInteger = 0
        pass
        
    def onBirth(self):
        # TODO Called soon after the actor is created and after it has been placed on a Stage.
        pass

    def onAttach(self):
        # TODO This role is now attached to an actor. Similar to onBirth, but if an Actor changes Roles, then onBirth will only be
        # called once, whereas onAttach is called when the Actor's role is first set, and also whenever it is changed to a different Role.
        pass

    def tick(self):
        # TODO Called 60 times a second, and is where all the good stuff belongs!
        pass

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


