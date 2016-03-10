from common import * #@UnusedWildImport

properties = ArrayList()

class Menu(PlainSceneDirector) :

    def __init__(self) :
        self.inputStart =  Input.find("start")
    
    def tick(self) :
        if self.inputStart.pressed() :
            Itchy.getGame().startScene( "cavernQuest" )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


