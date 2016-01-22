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


