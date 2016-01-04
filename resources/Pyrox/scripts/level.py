from common import *

from grid import Grid
from gridRole import GridRole
from macroRecorder import MacroRecorder
from macroPlayback import MacroPlayback

properties = ArrayList()

game = Itchy.getGame()

# Globals used to allow the user to save and restore their location. Its done using MacroRecorder
recordedInput = ""
replayInput = False

# The SceneDirector used by all of the play levels.
#
# After a scene has loaded, places all GridRole objects into a Grid. Without this grid,
# objects wouldn't be able to look around them, and react with each other.
#
# Keeps track of the number of collectables remaining.
#
# When there are more than one Player in a Scene, this controls which one is awake and
# which are sent to sleep.
#
# 
class Level(PlainSceneDirector) :

    def __init__(self) :
    
        self.inputToggleInfo = Input.find("toggleInfo")
        self.inputTest = Input.find("runTests")

        # Record and playback the player's moves using the MacroRecorder and MacroPlayback
        self.inputSave = Input.find("save")
        self.inputLoad = Input.find("load")
                
        self.inputNextPlayer = Input.find("nextPlayer")
                
        self.collectablesRemaining = 0
        self.showInfo = True
        self.player = None
        
        self.macroRecorder = None
        self.macroPlayback = None

    def onActivate( self ) :

        for player in game.findRoleByTag("player") :
            if self.player is None or player.awake :
                self.player = player

        if self.player :
            director = game.director
            director.gridView.centerOn(self.player.actor)
            director.testView.centerOn(self.player.actor)
            director.plainView.centerOn(self.player.actor)

        self.droppedFramesRole = game.findRoleById("droppedFrames")

        self.toggleInfo()
                
        # Now that the scene has loaded, let the player find the position it should start in
        # This is used on the "play" scene, to allow the player to start near to the gate he
        # has just completed.
        # If there are more than one player, then the others will go to sleep.
        for player in game.findRoleByTag("player") :
            player.getReady( player == self.player )
    
        for portcullis in game.findRoleByTag("portcullis") :
            portcullis.getReady(self.player)
            
        self.macroRecorder = MacroRecorder()
        self.macroRecorder.startRecording()

        global replayInput
        global recordedInput
        if replayInput :
            replayInput = False
            self.macroRecorder.recorded = recordedInput
            self.macroPlayback = MacroPlayback( recordedInput )

        
    def onLoaded( self ) :
        
        # Load the glass stage on top of the current scene.
        game.loadScene("glass", True)
        
        # Calculate the size of the grid needed to fit all of the actors
        stage = game.director.gridStage

        minX = 1000000
        minY = 1000000
        maxX = -1000000
        maxY = -1000000

        i = stage.iterator()
        if ( not i.hasNext() ) :
            minX = 0
            minY = 0
            maxX = 0
            maxY = 0
        
        while (i.hasNext()) :
            actor = i.next()
            x = actor.getX()
            y = actor.getY()

            if x < minX :
                minX = x

            if x > maxX :
                maxX = x

            if y < minY :
                minY = y

            if y > maxY :
                maxY = y

        squareSize = game.director.squareSize
        across = math.floor( (maxX - minX) / squareSize) + 1
        down = math.floor( (maxY - minY) / squareSize) + 1
        
        self.grid = Grid( squareSize, across, down, minX, minY )
        game.director.gridStage.grid = self.grid
        
        # Add all of the GridRoles to the grid
        i = stage.iterator()
        while (i.hasNext()) :
            actor = i.next()
            role = actor.role
            if isinstance( role, GridRole ) :
                if not role.actor.isDead() :
                    role.placeOnGrid( self.grid )

    def tick(self) :

        if self.player :
            self.player.playerTick()
                
        PlainSceneDirector.tick(self)

        if self.macroPlayback :
            self.macroPlayback.tick()
        
        if self.macroRecorder :
            self.macroRecorder.tick()
        
    def onKeyDown(self, kevent) :
    
        if self.inputToggleInfo.matches(kevent) :
            self.toggleInfo()
    
        if self.inputTest.matches(kevent) :
            self.runTests()
            
        if self.inputNextPlayer.matches(kevent) :
            self.wakeNextPlayer()
            
        if self.inputSave.matches(kevent) :
            self.saveGame()
            
        if self.inputLoad.matches(kevent) :
            self.loadGame()
            
            
    def playerDied( self, player ) :
        if self.player is player :
            self.wakeNextPlayer()


    def wakeNextPlayer(self) :
        
        previous = None
        for player in game.findRoleByTag("player") :
                
            if player is self.player and previous is not None :
                self.wakePlayer( previous )
                return

            if player.actor.isDying() or player.actor.isDead() :
                pass
            else :
                previous = player

        self.wakePlayer(previous)
        
    def wakePlayer( self, player ) :
        if player is None :
            # All players are dead!
            return
            
        if player is not self.player :
            self.player.sleep()
            self.player = player
            self.player.wake()


    def saveGame(self) :
        global recordedInput
        recordedInput = self.macroRecorder.getRecording()
        print "Recorded: ", recordedInput


    def loadGame(self) :
        global replayInput
        replayInput = True
        game.startScene(game.sceneName)
        

    def runTests(self) :
    
        for autoPilot in Itchy.getGame().findRoleByTag("autoPilot") :
            autoPilot.run()
            return
            
        game.startScene("test")
    
    def toggleInfo( self ) :

        self.showInfo = not self.showInfo
        alpha = 255 if self.showInfo else 0

        if self.droppedFramesRole :
            self.droppedFramesRole.actor.appearance.alpha = alpha
        
    # When the number of collectables remaining is zero, tell all gates to open.
    def collected( self, amount ) :
        self.collectablesRemaining -= amount
        if self.collectablesRemaining <= 0 :

            for gate in game.findRoleByTag( "gate" ) :
                gate.onMessage("open")


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


