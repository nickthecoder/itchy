from common import *
from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty( "moves" ) )
properties.add( BooleanProperty( "killOnPressed" ) )
properties.add( BooleanProperty( "autoTest" ) )

game = Itchy.getGame()

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

    def onSceneCreated(self) :
        if self.autoTest :
            self.run()

    def run(self) :

        # The extra collectable will be gained if no tests fail
        # This will prevent the gate opening if a test fails, and therefore remain on this level.
        game.sceneDirector.collected(-1) 

        self.testing = True
        for player in game.findRoleByTag("player") :
            self.runPlayer( player )

    def runPlayer(self,player) :
        self.player = player

        self.player.inputLeft = FakeInput( self, "l" )
        self.player.inputUp = FakeInput( self, "u" )
        self.player.inputDown = FakeInput( self, "d" )
        self.player.inputRight = FakeInput( self, "r" )
                
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
                    self.testing = False
                        
            if self.movesIndex >= len( self.moves ) :

                # Revert to manual control
                if self.player :
                    self.player.inputLeft = Input.find("left")
                    self.player.inputRight = Input.find("right")
                    self.player.inputUp = Input.find("up")
                    self.player.inputDown = Input.find("down")
                self.actor.kill()

    def runTests(self) :
    
        game.director.layout.findView("test").setVisible(True)
        print "Running tests"
        count = 0
        fails = 0
        for test in game.findRoleByTag("test") :
            count += 1
            if not test.run() :
                fails += 1
        print "Ran", count, "tests", fails, "failures"
        if fails == 0 :
            game.sceneDirector.collected(1)

    def canShove( self, pusher, dx, dy, speed, force ) :
    
        if pusher.hasTag("player") :
            if self.killOnPressed :
                self.actor.appearance.alpha = 1
                self.removeFromGrid()
            self.runPlayer(pusher)

        return False


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
    
        
