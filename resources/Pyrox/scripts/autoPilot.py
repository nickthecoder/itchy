from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import BooleanProperty

from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty( "moves" ) )
properties.add( BooleanProperty( "killOnPressed" ) )
properties.add( BooleanProperty( "autoTest" ) )

# Automates the Player, so that his movements are automatically made based upon the "moves" property of this object.
# "moves" is a string, with the letters "u","d","l","r" used for up,down,left and right.
class AutoPilot(GridRole) :

    def __init__(self) :
        super(AutoPilot,self).__init__()
        self.moves = ""
        self.advanceIndex = False
        self.testing = False
        self.killOnPressed = True
        self.autoTest = False
        
    def onBirth(self) :
        super(AutoPilot,self).onBirth()
        self.addTag("autoPilot")
        self.moves = self.moves.replace("-", "          ")
        if self.autoTest :
            self.run()

    def run(self) :

        self.testing = True
        for player in Itchy.getGame().findRoleByTag("player") :
            self.runPlayer( player )

    def runPlayer(self,player) :
        self.player = player

        self.player.inputLeft = FakeInput( self, "l" )
        self.player.inputUp = FakeInput( self, "u" )
        self.player.inputDown = FakeInput( self, "d" )
        self.player.inputRight = FakeInput( self, "r" )
        print "Auto Pilot engaged"
                
        self.movesIndex = 0

    def fakePressed(self, letter) :
        self.advanceIndex = True
        
        if self.movesIndex >= len( self.moves ) :
            return False

        return letter == self.moves[ self.movesIndex ]

    def tick(self) :
    
        if self.advanceIndex :
            self.advanceIndex = False
            self.movesIndex += 1

            if self.movesIndex >= len( self.moves ) -1 and self.testing :
                if self.testing :
                    self.runTests()
                        
            if self.movesIndex >= len( self.moves ) :

                # Revert to manual control
                if self.player :
                    self.player.inputLeft = Input.find("left")
                    self.player.inputRight = Input.find("right")
                    self.player.inputUp = Input.find("up")
                    self.player.inputDown = Input.find("down")
                print "Auto Pilot disengaged"
                self.actor.kill()

    def runTests(self) :
    
        Itchy.getGame().director.testView.setVisible(True)
        print "Running tests"
        Itchy.getGame().getSceneDirector().collected(-1) # The extra collectable will be gained if no tests fail
        count = 0
        fails = 0
        for test in Itchy.getGame().findRoleByTag("test") :
            count += 1
            if not test.run() :
                fails += 1
        print "Ran", count, "tests", fails, "failures"
        if fails == 0 :
            Itchy.getGame().getSceneDirector().collected(1)

    def canShove( self, pusher, dx, dy, speed, force ) :
    
        if pusher.hasTag("player") :
            if self.killOnPressed :
                self.actor.appearance.alpha = 1
                self.removeFromGrid()
            self.runPlayer(pusher)

        return False

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class FakeInput :

    def __init__(self, autoPilot, letter) :
        self.autoPilot = autoPilot
        self.letter = letter
        
    def pressed(self) :
        return self.autoPilot.fakePressed( self.letter )
        
    def matches(self,ke) :
        return self.autoPilot.fakePressed( self.letter )
    
        
