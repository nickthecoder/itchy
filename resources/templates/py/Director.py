from uk.co.nickthecoder.itchy import AbstractDirector
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

class ${Name}(AbstractDirector) :

    def __init__(self) :
        # TODO Initialise your object.
        pass

    def tick(self) :
        # TODO Called 60 times a second.
        # It is often better to put game logic in SceneDirector subclasses, rather than Director subclasses.
        pass
    
    # TODO Other methods include :
    # onStarted, onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onQuit, onMessage

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


