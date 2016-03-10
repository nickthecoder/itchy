from common import * #@UnusedWildImport

properties = ArrayList()
class LoadEventsRole(AbstractRole) :

    def __init__(self) :
        self.events = []
        
    def onBirth(self):
        self.event( "onBirth" )

        game.sceneDirector.directorEvent()
        self.event( "onActivate" )
        
    def onSceneCreated(self) :
        self.event( "onSceneCreated" )
        
    def tick(self):
        self.event( "tick" )

    def event(self,text) :
        if hasattr(game.sceneDirector, 'event') :
            game.sceneDirector.directorEvent()
            game.sceneDirector.event("role." + text)

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


