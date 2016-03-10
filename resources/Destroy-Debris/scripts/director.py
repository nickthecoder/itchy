from common import * #@UnusedWildImport

game = Itchy.getGame()

class Director(AbstractDirector) :

    def __init__(self) :
        self.score = 0
        self.lives = 3
      
    def onStartingScene(self, sceneName) :
        
        if game.pause.isPaused() :
            game.pause.unpause()
            
        if sceneName == "menu" and game.getSceneName() == "menu" :
            return True
        
        transition = SceneTransition.fade()
        
        if game.getSceneName() == "menu" :
            transition = SceneTransition.slideRight()

        if sceneName == "menu" :
            transition = SceneTransition.slideLeft()
            self.reset();

        self.sceneTransition = SceneTransition(transition)
        self.sceneTransition.prepare()

    def onStartedScene(self) :        
        self.sceneTransition.begin()


    def reset(self) :
        self.score = 0
        self.lives = 3
    
    
    def onMessage(self, message) :
        if message == "start" :
            self.reset();
            self.startScene("easy-1")
        
        if message == "continue" :
            self.reset();
            self.startScene(game.getSceneName())
    
    
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
        return ClassName( Director, self.__module__ + ".py" )

