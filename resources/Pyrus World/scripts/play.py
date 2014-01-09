from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Timer

from java.util import ArrayList

properties = ArrayList()

class Play(PlainSceneDirector) :

    def __init__(self) :
        self.rocks = 0 # count the rocks on the screen, when it goes down to zero, next level!
        self.endTimer = None
        self.ship = None # Set by Ship's onBirth.

    def onActivate(self) :
        self.game = Itchy.getGame()
        self.game.loadScene("foreground", True)

    def onKeyDown(self, event) :
        # Escape key takes us back to the menu.
        if event.symbol == event.ESCAPE :
            self.game.startScene("menu")
            return True; # Return true to indicate that the key has been processed.

        # Play again if dead an return pressed.
        if event.symbol == event.RETURN and Itchy.getGame().getDirector().lives == 0 :
            self.game.getDirector().startGame()
    
        if event.symbol > event.KEY_0 and event.symbol <= event.KEY_9 :
            self.game.startScene( str(event.symbol - event.KEY_0) )
        
        if event.symbol == event.p :
            self.game.pause.togglePause()
        
        return False
    
    def addRocks(self, diff) :

        self.rocks += diff
        print "Rocks remaining : ", self.rocks
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
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


