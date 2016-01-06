from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Director, AbstractDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import StageView
from uk.co.nickthecoder.itchy import ZOrderStage

from uk.co.nickthecoder.jame import Rect

from java.util import ArrayList
from java.util import Date

class Director(AbstractDirector) :

    def __init__(self) :
        pass

    def onStarted( self ) :

        self.inputExit = Input.find("exit")

        self.startTime = Date().time
        self.time = 0
            
        screenRect = Rect(0, 0, self.game.getWidth(), self.game.getHeight())

        self.bottomStage = ZOrderStage("bottom")
        self.game.getStages().add(self.bottomStage)
        self.bottomView = StageView(screenRect, self.bottomStage)
        self.game.getGameViews().add(self.bottomView)

        self.middleStage = ZOrderStage( "middle" )
        self.game.getStages().add(self.middleStage)
        self.middleView = StageView(screenRect, self.middleStage)
        self.game.getGameViews().add(self.middleView)

        self.topStage = ZOrderStage("top")
        self.game.getStages().add(self.topStage)
        self.topView = StageView(screenRect, self.topStage)
        self.game.getGameViews().add(self.topView)

        self.topView.enableMouseListener(self.game)
        
    def tick(self) :
        if self.inputExit.pressed() :
            self.startScene( "start" )
        self.time = (Date().time - self.startTime) / 1000
        
    def startScene( self, sceneName ) :
        self.sceneName = sceneName
        return AbstractDirector.startScene( self, sceneName )
        
    def restartScene( self ) :
        self.startScene( self.sceneName )

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


