from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import AbstractDirector
from uk.co.nickthecoder.itchy import StageView
from uk.co.nickthecoder.itchy import WrappedStageView
from uk.co.nickthecoder.itchy import ZOrderStage
from uk.co.nickthecoder.itchy.collision import WrappedCollisionStrategy
from uk.co.nickthecoder.itchy.extras import SceneTransition
from uk.co.nickthecoder.itchy.extras import SimpleMousePointer
from uk.co.nickthecoder.itchy.role import Explosion
from uk.co.nickthecoder.itchy.role import OnionSkin
from uk.co.nickthecoder.itchy.util import ClassName

from uk.co.nickthecoder.jame import Rect

from java.util import ArrayList

class Director(AbstractDirector) :

    def __init__(self) :
        self.score = 0
        self.lives = 3

    def onStarted(self) :
        self.socre = 0
        self.lives = 3
        
        # Don't create default stages and views, because we want to use a special WrappedStageView
        print "Creating custom stages and views"

        screenRect = Rect(0, 0, self.game.getWidth(), self.game.getHeight())

        self.mainStage = ZOrderStage("main")
        self.game.getStages().add(self.mainStage)

        # We need a separate view for the "lives" in the top left, because we don't want those to
        # wrap round to the bottom of the screen.
        self.hudStage = ZOrderStage("hud")
        self.game.getStages().add(self.hudStage)

        self.mainView = WrappedStageView(screenRect, self.mainStage)
        self.mainView.wrap( self.game.getHeight(), self.game.getWidth(), 0, 0 )
        self.game.getGameViews().add(self.mainView)

        self.hudView = StageView(screenRect, self.hudStage)
        self.game.getGameViews().add(self.hudView)

        self.hudView.enableMouseListener(self.game)
        self.collisionStrategy = WrappedCollisionStrategy( self.mainView )

    def tick(self) :
        pass
      
    def startScene(self, sceneName) :
        print "Starting scene :", sceneName
        
        if self.getGame().pause.isPaused() :
            self.getGame().pause.unpause()
            
        if sceneName == "menu" and self.getGame().getSceneName() == "menu" :
            return True;
        
        transition = SceneTransition.fade();
        if self.getGame().getSceneName() == "menu" :
            transition = SceneTransition.slideRight()

        if sceneName == "menu" :
            transition = SceneTransition.slideLeft()
            self.score = 0
            self.lives = 3

        return SceneTransition(transition).transition(sceneName)
    

    def onMessage(self, message) :
        if message == "start" :
            self.startGame("1")
        
        if message == "continue" :
            self.startGame(self.getGame().getSceneName())
    
    def addPoints(self, points ) :
        self.score += points;

    def onWindowEvent(self, event) :
        if event.lostMouseFocus() :
            self.getGame().mouse.showRegularMousePointer( True )
            return True
        elif event.gainedMouseFocus() :
            self.showFancyMouse()
            return True
        return False

    def showFancyMouse(self) :
        mousePointer = SimpleMousePointer("mouse");
        self.getGame().mouse.setMousePointer( mousePointer );
        
        Explosion(mousePointer.getActor()).dependent().forever().follow().offset(40,-33).projectilesPerTick(1) \
            .spread(-20,-80).distance(10).randomSpread().speed(1,2,0,0).fade(3).eventName("spark") \
            .createActor()
        OnionSkin(mousePointer.getActor()).alpha(128).fade(3).every(1).createActor()

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )

 
