from common import * #@UnusedWildImport

from gridStage import GridStage

game = Itchy.getGame()

class Director(AbstractDirector) :

    def __init__(self) :
        
        self.squareSize = 36
    
    
    def onStarted( self ) :

        self.inputRestart = Input.find("restart")
        self.inputQuit = Input.find("quit")
        
            
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
    
    
    def onKeyDown(self,kevent) :
         
        if self.inputRestart.matches(kevent) :
            Itchy.getGame().startScene( Itchy.getGame().getSceneName() )

        if self.inputQuit.matches(kevent) :
            Itchy.getGame().end()
        
        # Call the base class. Note that super(Director,self).onKeyDown(kevent) throws an exception :-(        
        AbstractDirector.onKeyDown(self, kevent)


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


