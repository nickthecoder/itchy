from uk.co.nickthecoder.itchy import AbstractDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import StageView
from uk.co.nickthecoder.itchy import ZOrderStage
from uk.co.nickthecoder.itchy import GridStageConstraint

from uk.co.nickthecoder.jame import Rect
from uk.co.nickthecoder.jame.event import Keys

from java.util import ArrayList

class Director(AbstractDirector) :

    def __init__(self) :
        self.squareSize = 60

    
    def onStarted( self ) :
        screenRect = Rect(0, 0, self.game.getWidth(), self.game.getHeight())

        self.plainStage = ZOrderStage("plain")
        self.game.getStages().add(self.plainStage)
        self.plainView = StageView(screenRect, self.plainStage)
        self.game.getGameViews().add(self.plainView)
        self.plainView.enableMouseListener(self.game)

        self.gridStage = ZOrderStage("grid")
        self.game.getStages().add(self.gridStage)
        self.gridView = StageView(screenRect, self.gridStage)
        self.game.getGameViews().add(self.gridView)

        self.glassStage = ZOrderStage("glass")
        self.game.getStages().add(self.glassStage)
        self.glassView = StageView(screenRect, self.glassStage)
        self.game.getGameViews().add(self.glassView)

        self.gridStage.setStageConstraint( GridStageConstraint( self.squareSize, self.squareSize ) )      

    
    def tick(self) :
    
        if Itchy.isKeyDown( Keys.F11 ) :
            Itchy.getGame().startScene( "test" )

        if Itchy.isKeyDown( Keys.F12 ) :
            sceneName = Itchy.getGame().getSceneName()
            if sceneName == "menu" :
                Itchy.getGame().startEditor()
            else :
                Itchy.getGame().startEditor( sceneName )
    
    # TODO Other methods include :
    # onStarted, onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onQuit, onMessage

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


