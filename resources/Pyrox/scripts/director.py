from common import *

from gridStage import GridStage

game = Itchy.getGame()

class Director(AbstractDirector) :

    def __init__(self) :
        
        self.squareSize = 60
                
        self.previousSceneName = ""
        self.macroRecord = False
        self.macroPlayback = False
    
    def onStarted( self ) :

        self.inputEditor = Input.find( "editor" )
        self.inputTest = Input.find( "test" )
        self.inputRestart = Input.find("restart")
        self.inputQuit = Input.find("quit")
        self.inputReset = Input.find("reset")
        self.inputRecord = Input.find("macroRecord")
        self.inputPlayback = Input.find("macroPlayback")

    
            
    def onMessage(self, message) :
        print "Message", message
        if message == Director.SPRITE_SHEETS_LOADED :
            self.processSprites()

    def centerOn( self, x, y ) :
        self.centerViewOn( game.layout.findView("plain"), x, y )
        self.centerViewOn( game.layout.findView("grid"), x, y )
        self.centerViewOn( game.layout.findView("test"), x, y )

    def processSprites( self ) :
        print "Processing Sprites"
        self.flipSprites( "robot-R", "robot-L", 4 )
        
    def flipSprites( self, prefix, newPrefix, n ) :
        resources = game.resources
        
        for i in range( 0, n ) :
            name = prefix + `i`
            pose = resources.getPose( name )
            if pose is None :
                print "Pose", name, "not found"
            else :
                newName = newPrefix + `i`
                newPose = ImagePose( pose.surface.zoom( -1,1, False ), pose.offsetX, pose.offsetY )
                newPR = DynamicPoseResource( newName, newPose )
                resources.addPose( newPR )
                print "Created pose", newName

    # Centers the view on the given coordinate, not straight away, but if called every frame, then
    # it will slowly home in on the required position.
    def centerViewOn( self, view, x, y ) :
        rect = view.getVisibleRectangle()
        reqX = x - rect.width / 2
        reqY = y - rect.height / 2
        
        weight = 20 # A higher number will add more lag, i.e. slower to home in on the new center
        newX = (rect.x * weight + reqX) / (weight + 1) # Weighted average of the required and current positions.
        newY = (rect.y * weight + reqY) / (weight + 1)
        
        view.scrollTo( newX, newY );
    
    def onStartingScene( self, sceneName ) :
        print "starting scene", sceneName
    
        # If we were playing a macro, or recording a macro, then stop.
        # TODO Itchy.eventProcessor.end()

        if self.macroRecord :
            MacroRecord( self.macroFile(sceneName) ).begin()
            self.macroRecord = False

        if self.macroPlayback :
            macro = self.macroFile(sceneName)
            if macro.exists() :
                print "Playing back macro"
                MacroPlayback( macro ).begin()
            else :
                print "Macro not found"


    def macroFile( self, sceneName ) :
        return game.resources.resolveFile( File( File("macros"), sceneName + ".macro" ) )
            
    def returnToGateRoom( self, warpRoom ) :
        
        self.previousSceneName = game.getSceneName()
        game.startScene( warpRoom )

        
    def onKeyDown(self,kevent) :
            
        if self.inputRecord.matches(kevent) :
            self.macroRecord = True
            self.macroPlayback = False
            print "Will start recording macro as soon as you start a new scene"


        if self.inputPlayback.matches(kevent) :
            self.macroRecord = False
            self.macroPlayback = True
            print "Will start playing back the macro as soon as you start a new scene"

        if self.inputTest.matches(kevent) :
            game.startScene( "test" )

        if self.inputEditor.matches(kevent) :
            sceneName = Itchy.getGame().getSceneName()
            game.startEditor( sceneName )
    
        if self.inputRestart.matches(kevent) :
            Itchy.getGame().startScene( Itchy.getGame().getSceneName() )

        if self.inputQuit.matches(kevent) :
            scene = game.sceneName
            if scene == "welcome" :
                Itchy.getGame().end()
            elif scene == "play" :
                game.startScene("welcome")
            else :
                game.startScene( "play" )

        if self.inputReset.matches(kevent) :
            game.preferences.removeNode()
        
        # Call the base class. Note that super(Director,self).onKeyDown(kevent) throws an exception :-(        
        AbstractDirector.onKeyDown(self, kevent)


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


