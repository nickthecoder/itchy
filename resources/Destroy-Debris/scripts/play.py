from common import *

properties = ArrayList()
properties.add( StringProperty("nextScene").hint("The name of the next scene") )

game = Itchy.getGame()
director = game.getDirector()

class Play(PlainSceneDirector) :

    def __init__(self) :
        self.rocks = 0 # count the rocks on the screen, when it goes down to zero, next level!
        self.endTimer = None
        self.ship = None # Set by Ship's onBirth.
        self.nextScene = "completed"

        self.inputExit = Input.find("exit")
        self.inputPause = Input.find("pause")
        self.inputRestart = Input.find("restart")
        self.inputContinue = Input.find("continue")


    def onLoaded(self) :
        print "Loading glass"
        game.loadScene("glass", True)


    def onKeyDown(self, event) :
    
        # Escape key takes us back to the menu.
        if self.inputExit.matches(event) :
            game.startScene("menu")
            event.stopPropagation()

        if director.lives == 0 :
        
            # Play again if dead an return pressed.
            if self.inputRestart.matches(event) :
                director.startGame()

            if self.inputContinue.matches(event) :
                director.startGame(game.getSceneName())
    
        if self.inputPause.matches(event) :
            game.pause.togglePause()
    
    
    def addRocks(self, diff) :

        self.rocks += diff
        if self.rocks == 0 :
            if self.ship != None and self.ship.getActor().isAlive() :
                self.endTimer = Timer.createTimerSeconds(3)
                self.ship.warp()


    def tick(self) :
    
        if self.endTimer and self.endTimer.isFinished() :
            if game.hasScene( self.nextScene ) :
                game.startScene( self.nextScene )
            else :
                game.startScene("completed")   
    
    
    def getCollisionStrategy(self,actor) :
        return director.collisionStrategy
    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )


