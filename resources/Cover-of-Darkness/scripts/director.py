from common import *

class Director(AbstractDirector) :

    def __init__(self) :
        pass

    def restartScene( self ) :
        game.startScene( game.sceneName )

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


