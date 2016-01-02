from common import *

game = Itchy.getGame()

class Director(AbstractDirector) :

    def __init__(self) :
        self.score = 0
        self.lives = 3


    def onStarted(self) :
        self.socre = 0
        self.lives = 3
        
        # Don't create default stages and views, because we want to use a special WrappedStageView
        print "Creating custom stages and views"

        screenRect = Rect(0, 0, game.getWidth(), game.getHeight())

        self.mainStage = ZOrderStage("main")
        game.getStages().add(self.mainStage)

        # We need a separate view for the "lives" in the top left, because we don't want those to
        # wrap round to the bottom of the screen.
        self.hudStage = ZOrderStage("hud")
        game.getStages().add(self.hudStage)

        self.mainView = WrappedStageView(screenRect, self.mainStage)
        self.mainView.wrap( game.getHeight(), game.getWidth(), 0, 0 )
        game.getGameViews().add(self.mainView)

        self.hudView = StageView(screenRect, self.hudStage)
        game.getGameViews().add(self.hudView)

        self.hudView.enableMouseListener(game)
        self.collisionStrategy = WrappedCollisionStrategy( self.mainView )

      
    def startScene(self, sceneName) :
        
        if game.pause.isPaused() :
            game.pause.unpause()
            
        if sceneName == "menu" and game.getSceneName() == "menu" :
            return True
        
        transition = SceneTransition.fade()
        if game.getSceneName() == "menu" :
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
            self.startGame(game.getSceneName())
    
    
    def addPoints(self, points ) :
        self.score += points


    def onWindowEvent(self, event) :
        if event.lostMouseFocus() :
            game.mouse.showRegularMousePointer( True )
            return True
        elif event.gainedMouseFocus() :
            self.showFancyMouse()
            return True
        return False


    def showFancyMouse(self) :
        mousePointer = SimpleMousePointer("mouse")
        game.mouse.setMousePointer( mousePointer )
        
        ExplosionBuilder(mousePointer.getActor()) \
            .dependent().forever().follow().offset(40,-33).projectilesPerTick(1) \
            .spread(-20,-80).distance(10).randomSpread().speed(1,2,0,0).fade(3).eventName("spark") \
            .create()
            
        OnionSkinBuilder(mousePointer.getActor()) \
            .alpha(128).fade(3).every(1).create()


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )

 
