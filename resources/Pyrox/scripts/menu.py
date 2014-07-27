from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from level import Level

properties = ArrayList()

class Menu(Level) :

    def onActivate(self) :
        super(Menu,self).onActivate()
        self.inputPlay = Input.find("play")
        
    def onKeyDown(self,ke):

        if self.inputPlay.matches(ke) :
            Itchy.getGame().startScene("play")

        super(Menu,self).onKeyDown(ke)
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


