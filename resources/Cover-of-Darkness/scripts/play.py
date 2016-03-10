from common import *
from java.util import Date

properties = ArrayList()

class Play(PlainSceneDirector) :

    def __init__(self) :
        # Collectables increment this counter when they are born.
        # Player compares it to the number he has collected
        self.collectables = 0
        
    def onActivate(self) :
        self.inputExit = Input.find("exit")
            
        self.inputScrollLeft = Input.find("scrollLeft")
        self.inputScrollRight = Input.find("scrollRight")
        self.inputScrollUp = Input.find("scrollUp")
        self.inputScrollDown = Input.find("scrollDown")

        self.startTime = Date().time
        self.time = 0
        
    def loading(self, scene) :
        print "Loading glass"
        game.mergeScene( "glass")


    def tick(self) :
        
        if self.inputExit.pressed() :
            game.startScene( "start" )
        
        self.time = (Date().time - self.startTime) / 1000

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

    def caught(self) :
        game.director.restartScene()
            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )


