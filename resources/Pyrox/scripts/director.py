from uk.co.nickthecoder.itchy import AbstractDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import StageView
from uk.co.nickthecoder.itchy import ZOrderStage
from uk.co.nickthecoder.itchy import GridStageConstraint
from uk.co.nickthecoder.itchy import Input

from uk.co.nickthecoder.jame import Rect

from java.util import ArrayList

from gridStage import GridStage

class Director(AbstractDirector) :

    def __init__(self) :
        
        self.squareSize = 60
        
        self.inputEditor = Input.find( "editor" )
        self.inputTest = Input.find( "test" )
        self.inputRestart = Input.find("restart")
        self.inputQuit = Input.find("quit")

        self.previousSceneName = ""
    
    def onStarted( self ) :
    
        screenRect = Rect(0, 0, self.game.getWidth(), self.game.getHeight())

        self.plainStage = ZOrderStage("plain")
        self.game.getStages().add(self.plainStage)
        self.plainView = StageView(screenRect, self.plainStage)
        self.game.getGameViews().add(self.plainView)
        self.plainView.enableMouseListener(self.game)

        self.gridStage = GridStage( "grid" ) # ZOrderStage("grid")
        self.game.getStages().add(self.gridStage)
        self.gridView = StageView(screenRect, self.gridStage)
        self.game.getGameViews().add(self.gridView)

        self.glassStage = ZOrderStage("glass")
        self.game.getStages().add(self.glassStage)
        self.glassView = StageView(screenRect, self.glassStage)
        self.game.getGameViews().add(self.glassView)

        self.gridStage.setStageConstraint( GridStageConstraint( self.squareSize, self.squareSize ) )      

    def returnToGateRoom( self, warpRoom ) :
        
        self.previousSceneName = Itchy.getGame().getSceneName()
        Itchy().getGame().startScene( warpRoom )

        
    def onKeyDown(self,kevent) :
    
        if self.inputTest.matches(kevent) :
            Itchy.getGame().startScene( "test" )

        if self.inputEditor.matches(kevent) :
            sceneName = Itchy.getGame().getSceneName()
            if sceneName == "menu" :
                Itchy.getGame().startEditor()
            else :
                Itchy.getGame().startEditor( sceneName )
    
        if self.inputRestart.matches(kevent) :
            Itchy.getGame().startScene( Itchy.getGame().getSceneName() )

        if self.inputQuit.matches(kevent) :
            Itchy.getGame().startScene( "menu" )

        # Call the base class. Note that super(Director,self).onKeyDown(kevent) throws an exception :-(        
        AbstractDirector.onKeyDown(self, kevent)


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


