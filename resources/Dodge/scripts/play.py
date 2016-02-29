from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "speedUp" ) )

class Play(PlainSceneDirector) :

    def __init__(self) :
        self.speedUp = 0 # How much to add to enemy speed each time they bounce.
        self.playing = False # Set to false when the player hits something.
        self.score = 0
    
    def onActivate(self) :
        self.inputExit = Input.find('exit')
        
    def tick(self) :
        if self.inputExit.pressed() :
            game.director.startScene("menu")

        if self.playing :
            self.score += 1

    def stopPlaying( self ) :
        self.playing = False
        self.speedUp = 0


    def loading( self, scene ) :
        game.mergeScene("glass")


    def onMessage( self, message ) :
        if message == "start" :
            self.playing = True
        Itchy.getGame().findActorById( "start" ).deathEvent("clicked")

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )



