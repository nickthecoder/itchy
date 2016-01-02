from common import *

game = Itchy.getGame()

class Director(AbstractDirector) :

    def onStarted( self ) :
        guiHeight = 100
        playRect = Rect(0, 0, game.getWidth(), game.getHeight() - guiHeight)
        guiRect = Rect(0, game.getHeight() - guiHeight, game.getWidth(), guiHeight )        

        self.gridStage = ZOrderStage("grid")
        game.getStages().add(self.gridStage)
        self.gridView = StageView( playRect, self.gridStage )
        game.getGameViews().add(self.gridView)
        self.gridStage.setStageConstraint( GridStageConstraint( 10,10 ) )
        self.gridView.enableMouseListener(game)

        self.mainStage = ZOrderStage("main")
        game.getStages().add(self.mainStage)
        self.mainView = StageView(playRect, self.mainStage)
        game.getGameViews().add(self.mainView)
        self.mainView.enableMouseListener(game)

        self.guiStage = ZOrderStage("gui")
        game.getStages().add(self.guiStage)
        self.guiView = StageView(guiRect, self.guiStage)
        game.getGameViews().add(self.guiView)
        self.guiView.enableMouseListener(game)


    def scrollTo( self, x, y ) :
        self.mainView.centerOn( x,y )
        self.gridView.centerOn( x,y )


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


