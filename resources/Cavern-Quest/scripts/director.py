from common import * #@UnusedWildImport

from gridStage import GridStage

game = Itchy.getGame()

class Director(AbstractDirector) :

    def __init__(self) :
        
        self.squareSize = 36
        self.macroRecord = False
        self.macroPlayback = False
    
    
    def onStarted( self ) :

        self.inputEditor = Input.find( "editor" )
        self.inputRestart = Input.find("restart")
        self.inputQuit = Input.find("quit")
        self.inputRecord = Input.find("macroRecord")
        self.inputPlayback = Input.find("macroPlayback")
        
            
    def centerOn( self, x, y ) :
        self.centerViewOn( game.layout.findView("plain"), x, y )
        self.centerViewOn( game.layout.findView("grid"), x, y )


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
    
    def startSceneXX( self, sceneName ) :
        AbstractDirector.startScene(self,sceneName)
        print "starting scene", sceneName
    
        # If we were playing a macro, or recording a macro, then stop.
        Itchy.eventProcessor.end()

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
        
    def onKeyDown(self,kevent) :
            
        if self.inputRecord.matches(kevent) :
            self.macroRecord = True
            self.macroPlayback = False
            print "Will start recording macro as soon as you start a new scene"

        if self.inputPlayback.matches(kevent) :
            self.macroRecord = False
            self.macroPlayback = True
            print "Will start playing back the macro as soon as you start a new scene"

        if self.inputEditor.matches(kevent) :
            sceneName = Itchy.getGame().getSceneName()
            game.startEditor( sceneName )
    
        if self.inputRestart.matches(kevent) :
            Itchy.getGame().startScene( Itchy.getGame().getSceneName() )

        if self.inputQuit.matches(kevent) :
            Itchy.getGame().end()
        
        # Call the base class. Note that super(Director,self).onKeyDown(kevent) throws an exception :-(        
        AbstractDirector.onKeyDown(self, kevent)


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


