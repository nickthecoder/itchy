from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Timer

from java.util import ArrayList

properties = ArrayList()

class Play(PlainSceneDirector) :

    def __init__(self) :
        self.game = Itchy.getGame()
        self.rocks = 0 # count the rocks on the screen, when it goes down to zero, next level!
        self.endTimer = None
        self.ship = None # Set by Ship's onBirth.

    def onActivate(self) :
    
        self.inputExit = Input.find("exit")
        self.inputPause = Input.find("pause")
        self.inputRestart = Input.find("restart")
        self.inputContinue = Input.find("continue")

        self.game.loadScene("foreground", True)

    def onKeyDown(self, event) :
    
        # Escape key takes us back to the menu.
        if self.inputExit.matches(event) :
            self.game.startScene("menu")
            return True; # Return true to indicate that the key has been processed.

        if Itchy.getGame().getDirector().lives == 0 :
        
            # Play again if dead an return pressed.
            if self.inputRestart.matches(event) :
                self.game.getDirector().startGame()

            if self.inputContinue.matches(event) :
                self.game.getDirector().startGame(self.game.getSceneName())
    
        if self.inputPause.matches(event) :
            self.game.pause.togglePause()
        
        return False
    
    def addRocks(self, diff) :

        self.rocks += diff
        if self.rocks == 0 :
            if self.ship != None and self.ship.getActor().isAlive() :
                self.endTimer = Timer.createTimerSeconds(3);
                self.ship.warp();

    def tick(self) :
    
        if self.endTimer and self.endTimer.isFinished() :
            nextScene = str(1 + int(self.game.getSceneName()))
            if self.game.hasScene( nextScene ) :
                self.game.startScene( nextScene )
            else :
                self.game.startScene("completed")   
    
    def getCollisionStrategy(self,actor) :
        return self.game.getDirector().collisionStrategy;
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


