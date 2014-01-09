from uk.co.nickthecoder.itchy import AbstractDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy.extras import SceneTransition
from uk.co.nickthecoder.itchy.extras import SimpleMousePointer
from uk.co.nickthecoder.itchy.role import Explosion
from uk.co.nickthecoder.itchy.role import OnionSkin

from java.util import ArrayList

class Director(AbstractDirector) :

    def __init__(self) :
        self.score = 0
        self.lives = 0

    def tick(self) :
        pass
      
    def startScene(self, sceneName) :
        print "Starting scene : ", sceneName
        if self.getGame().pause.isPaused() :
            self.getGame().pause.unpause()
            
        if sceneName == "menu" and self.getGame().getSceneName() == "menu" :
            return true;
        
        transition = SceneTransition.fade();
        if self.getGame().getSceneName() == "menu" :
            transition = SceneTransition.slideRight()

        if sceneName == "menu" :
            transition = SceneTransition.slideLeft()

        return SceneTransition(transition).transition(sceneName)
    

    def onMessage(self, message) :
        if message == "start" :
            self.startGame("1")
        
        if message == "continue" :
            self.startGame(self.getGame().getSceneName())
    
    def startGame(self, sceneName="1" ) :
        self.score = 0
        self.lives = 3
        self.startScene(sceneName)

    def addPoints(self, points ) :
        self.score += points;
    
    def showFancyMouse(self) :
        print "Showing fancy mouse"
        mousePointer = SimpleMousePointer("mouse");
        self.getGame().mouse.setMousePointer( mousePointer );
        
        Explosion(mousePointer.getActor()).forever().follow().offset(40,-33).projectilesPerTick(1) \
            .spread(-20,-80).distance(10).randomSpread().speed(1,2,0,0).fade(3).eventName("spark") \
            .createActor()
        OnionSkin(mousePointer.getActor()).alpha(128).fade(3).every(1).createActor()

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )

 
