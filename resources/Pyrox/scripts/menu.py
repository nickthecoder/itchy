from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

properties = ArrayList()

class Menu(PlainSceneDirector) :

    def __init__(self) :
        pass
    
    def tick(self) :
        pass
        
    def onActivate(self) :
        self.inputPlay = Input.find("play")
        
    def onKeyDown(self,ke):
        if self.inputPlay.matches(ke) :
            Itchy.getGame().startScene("play")
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


