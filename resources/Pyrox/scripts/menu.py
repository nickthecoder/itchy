from common import *

from level import Level

properties = ArrayList()

# Used only for the Scene "menu", and allows the "play" Scene to be started without by pressing Enter.
# Otherwise, it behaves just like a normal Level.
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


