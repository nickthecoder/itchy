from common import *

class Director(AbstractDirector) :

    def __init__(self) :
        pass

        
    def startScene( self, sceneName ) :
        self.sceneName = sceneName
        return AbstractDirector.startScene( self, sceneName )
        
    def restartScene( self ) :
        self.startScene( self.sceneName )

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


