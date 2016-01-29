from common import *

properties = ArrayList()

class Play(PlainSceneDirector) :

    def __init__(self) :
        # Collectables increment this counter when they are born.
        # Player compares it to the number he has collected
        self.collectables = 0
        
    def onActivate(self) :
        self.inputScrollLeft = Input.find("scrollLeft")
        self.inputScrollRight = Input.find("scrollRight")
        self.inputScrollUp = Input.find("scrollUp")
        self.inputScrollDown = Input.find("scrollDown")

    def tick(self) :
        if self.inputScrollLeft.pressed() :
            game.sceneDirector.scrollBy(-2,0)
        if self.inputScrollRight.pressed() :
            game.sceneDirector.scrollBy(2,0)
        if self.inputScrollUp.pressed() :
            game.sceneDirector.scrollBy(0,2)
        if self.inputScrollDown.pressed() :
            game.sceneDirector.scrollBy(0,-2)
            
    def scrollBy(self, x, y) :
        game.layout.findView("top").scrollBy( x, y )
        game.layout.findView("middle").scrollBy( x, y )
        game.layout.findView("bottom").scrollBy( x, y )

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )


