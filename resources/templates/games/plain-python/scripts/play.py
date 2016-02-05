from common import * #@UnusedWildImport

properties = ArrayList()
# TODO declare properties here. Note that you must also initialise them in __init__
# e.g.
# properties.add( StringProperty( "myString" ).hint("My green label" ) )

class Play(PlainSceneDirector) :

    def __init__(self) :
        # TODO Initialise here
        pass
    
    def onActivate(self) :
        self.inputExit = Input.find('exit')
        
    def tick(self) :
        if self.inputExit.pressed() :
            game.director.startScene("menu")

    # TODO Other methods include :
    # onMouseDown, onMouseUp, onMouseMove, onMessage
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )

