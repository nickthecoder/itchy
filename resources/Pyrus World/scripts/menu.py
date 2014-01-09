from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.jame.event import Keys

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
    
    def onKeyDown(self,event) :
        if event.symbol == Keys.RETURN :
            print("Return pressed")
            self.director.startGame()
            
        elif event.symbol == Keys.ESCAPE :
            self.game.startScene("menu")
            
        elif event.symbol == Keys.a :
        	self.game.startScene("about");
    
    # The menu has flying rocks, but we don't care about them, so do nothing.
    def addRocks(self, diff) :
        pass

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


