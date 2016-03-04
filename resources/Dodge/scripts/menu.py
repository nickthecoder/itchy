from common import * #@UnusedWildImport

properties = ArrayList()

class Menu(PlainSceneDirector) :

    def __init__(self) :
        self.playing = False
        
        
    def onActivate(self) :
        self.inputNext = Input.find('next')
        self.inputPrev = Input.find('prev')
        self.inputGo = Input.find('go')
        
    def onLoaded(self) :
        self.setSceneName( game.preferences.get("scene", "a") )


    def setSceneName(self, sceneName) :
        self.sceneName = sceneName
        game.findActorById("thumbnail").event( "unknown" )
        game.findActorById("thumbnail").event( sceneName )
        
        game.findActorById("name").appearance.pose.text = sceneName

        
    def onKeyDown(self, event) :
        # Press a letter to start the scene.
        if game.hasScene( event.c ) :
            game.director.startScene( event.c )
        
        if self.inputNext.matches(event) :
            self.changeScene( 1 )
            
        if self.inputPrev.matches(event) :
            self.changeScene( -1 )

        if self.inputGo.matches(event) :
            game.startScene( self.sceneName )
            
            
    def onMessage(self, message) :
        
        if message == "go" :
            game.startScene( self.sceneName )
            
        if message == "next" :
            self.changeScene( 1 )

        if message == "prev" :
            self.changeScene( -1 )

    
    def changeScene( self, delta ) :
        sceneName = chr( ord( self.sceneName ) + delta )
        if game.resources.getScene( sceneName ) is None :
            return

        self.setSceneName( sceneName )
        game.preferences.put("scene", sceneName)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


