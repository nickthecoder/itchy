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
        self.inputReset = Input.find("reset")
        
        self.previousSceneName = ""
    
    def onStarted( self ) :
    
        screenRect = Rect(0, 0, self.game.getWidth(), self.game.getHeight())

        self.plainStage = ZOrderStage("plain")
        self.game.getStages().add(self.plainStage)
        self.plainView = StageView(screenRect, self.plainStage)
        self.game.getGameViews().add(self.plainView)
        self.plainView.enableMouseListener(self.game)

        self.gridStage = GridStage( "grid" )
        self.game.getStages().add(self.gridStage)
        self.gridView = StageView(screenRect, self.gridStage)
        self.game.getGameViews().add(self.gridView)

        self.testStage = ZOrderStage( "test" )
        self.game.getStages().add(self.testStage)
        self.testView = StageView(screenRect, self.testStage)
        self.game.getGameViews().add(self.testView)
        self.testView.setVisible( False )

        self.glassStage = ZOrderStage("glass")
        self.game.getStages().add(self.glassStage)
        self.glassView = StageView(screenRect, self.glassStage)
        self.game.getGameViews().add(self.glassView)

        self.gridStage.setStageConstraint( GridStageConstraint( self.squareSize, self.squareSize ) )      
        self.testStage.setStageConstraint( GridStageConstraint( self.squareSize, self.squareSize ) )      


    def centerOn( self, x, y ) :
        self.centerViewOn( self.plainView, x, y )
        self.centerViewOn( self.gridView, x, y )
        self.centerViewOn( self.testView, x, y )

    # Centers the view on the given coordinate, not straight away, but if called every frame, then
    # it will slowly home in on the required position.
    def centerViewOn( self, view, x, y ) :
        rect = view.getVisibleRectangle()
        reqX = x - rect.width / 2
        reqY = y - rect.height / 2
        
        weight = 20 # A higher number will add more lag, i.e. slower to home in on the new center
        newX = (rect.x * weight + reqX) / (weight + 1) # Weighted average of the required and current positions.
        newY = (rect.y * weight + reqY) / (weight + 1)
        
        view.scrollTo( newX, newY );
    
    def returnToGateRoom( self, warpRoom ) :
        
        self.previousSceneName = Itchy.getGame().getSceneName()
        Itchy.getGame().startScene( warpRoom )

        
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
            scene = Itchy.getGame().sceneName
            if scene == "menu" :
                Itchy.terminate()
            elif scene == "play" :
                Itchy.getGame().startScene("menu")
            else :
                Itchy.getGame().startScene( "play" )

        if self.inputReset.matches(kevent) :
            Itchy.getGame().getPreferences().removeNode()
        
        # Call the base class. Note that super(Director,self).onKeyDown(kevent) throws an exception :-(        
        AbstractDirector.onKeyDown(self, kevent)


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


