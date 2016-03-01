from common import * #@UnusedWildImport

properties = ArrayList()

class Menu(PlainSceneDirector) :

    def __init__(self) :
        self.playing = False

    def onKeyDown(self, event) :
        # Press a letter to start the scene.
        if game.hasScene( event.c ) :
            game.director.startScene( event.c )
    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


