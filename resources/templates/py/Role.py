from common import *

properties = ArrayList()
# TODO declare poroperties here. Note that you must also initialise them in __init__
# e.g.
# properties.add( StringProperty( "myString" ).hint("My green label" ) )

class ${Name}(AbstractRole) :

    def __init__(self) :
        # TODO Initialise your object. Note you can't access self.getActor() yet.
        pass
        
    def onBirth(self):
        # TODO Called soon after the actor is created and after it has been placed on a Stage.
        pass

    def tick(self):
        # TODO Called 60 times a second, and is where all the good stuff belongs!
        pass


    # TODO Other methods include :
    # onSceneCreated, onDetach, onKill, onMouseDown, onMouseUp, onMouseMove, createCostumeProperties

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


