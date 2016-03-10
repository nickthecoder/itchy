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
        
        mousePointer = SimpleMousePointer("pointer")
        game.mouse.setMousePointer( mousePointer )

        
    def tick(self) :
        
        if self.inputRestart.pressed() :
            game.startScene( game.sceneName )

        if self.inputReset.pressed() :
            self.highScore = 0
            game.preferences.putInt( self.highScoreKey(), 0 )

        if self.inputExit.pressed() :
            game.startScene("menu")

        if self.playing :
            self.score += 1

            
        
    def stopPlaying( self ) :
        self.playing = False
        self.speedUp = 0
        if self.score >= self.highScore :
            game.preferences.putInt( self.highScoreKey(), self.score )
        game.mergeScene("gameOver")
        

    def loading( self, scene ) :
        thumbFile = game.resources.resolveFile( File( "images", "thumbnail-" + scene.name + ".png" ) )
        if not thumbFile.exists() :
            surface = Surface( game.width, game.height, True )
            game.render(surface);
            thumbnail = surface.rotoZoom( 0, 0.2, True )
            thumbnail.saveAsPNG( thumbFile.path )

        game.mergeScene("glass")


    def onMessage( self, message ) :

        if message == "start" :
            self.playing = True
            # Hide the "Start" and abort whatever animation is currently happening.
            game.findActorById( "start" ).deathEvent("clicked", None, AnimationEvent.REPLACE)
            
        if message == "restart" :
            game.startScene( game.sceneName )
            

    def highScoreKey(self) :
        return "highScore_" + game.sceneName
        

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )



