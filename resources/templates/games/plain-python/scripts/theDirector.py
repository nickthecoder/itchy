from common import * #@UnusedWildImport

class TheDirector (AbstractDirector) :

    def __init__(self) :
        # TODO Initialise your object.
        pass
    
    def tick(self ) :
        # TODO Called 60 times a second.
        # It is often better to put game logic in SceneDirector subclasses, rather than Director subclasses.
        pass
            
    # TODO Other methods include :
    # onStarted, onMouseDown, onMouseUp, onMouseMove,  onQuit, onMessage

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


