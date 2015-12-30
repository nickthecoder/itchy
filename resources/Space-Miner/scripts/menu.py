from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

properties = ArrayList()

class Menu(PlainSceneDirector) :
    
    def __init__(self) :
        pass
    
    def tick(self) :
        pass

    def onActivate(self) :
        self.game = Itchy.getGame()
        self.director = self.game.getDirector()
        self.director.showFancyMouse()

        self.inputStart = Input.find("start")
        self.inputMenu = Input.find("menu")
        self.inputAbout = Input.find("about")
        self.inputEditor = Input.find("editor")
    
    def onKeyDown(self,event) :
    
        if self.inputStart.matches(event) :
            self.director.startGame()
            
        elif self.inputMenu.matches(event) :
            self.game.startScene("menu")
            
        elif self.inputAbout.matches(event) :
        	self.game.startScene("about")
    
        elif self.inputEditor.matches(event) :
        	self.game.startEditor()
    
    # The menu has flying rocks, but we don't care about them, so do nothing.
    def addRocks(self, diff) :
        pass

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


