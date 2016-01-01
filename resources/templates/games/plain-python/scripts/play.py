from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

properties = ArrayList()

class Play(PlainSceneDirector) :

    def __init__(self) :
		# TODO Initialise here
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


