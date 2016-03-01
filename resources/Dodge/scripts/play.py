from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "speedUp" ) )

class Play(PlainSceneDirector) :

    def __init__(self) :
        self.speedUp = 0 # How much to add to enemy speed each time they bounce.
        self.playing = False # Set to false when the player hits something.
        self.score = 0
        self.highScore = -1
    
    
    def onActivate(self) :
        self.inputExit = Input.find('exit')
        self.inputRestart = Input.find('restart')
        self.inputReset = Input.find('reset')
        self.highScore = game.preferences.getInt( self.highScoreKey(), 0 )
        
        
    def tick(self) :
        if self.inputRestart.pressed() :
            game.director.startScene( game.sceneName )

        if self.inputReset.pressed() :
            self.highScore = 0
            game.preferences.putInt( self.highScoreKey(), 0 )

        if self.inputExit.pressed() :
            game.director.startScene("menu")

        if self.playing :
            self.score += 1


    def stopPlaying( self ) :
        self.playing = False
        self.speedUp = 0
        if self.score >= self.highScore :
            game.preferences.putInt( self.highScoreKey(), self.score )


    def loading( self, scene ) :
        game.mergeScene("glass")


    def onMessage( self, message ) :
        if message == "start" :
            self.playing = True
            game.findActorById( "start" ).deathEvent("clicked")

    def highScoreKey(self) :
        return "highScore_" + game.sceneName
        

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )



