from common import * #@UnusedWildImport

from test import Test
from uk.co.nickthecoder.itchy import PlainDirector
from testDirector import TestDirector

properties = ArrayList()

# Checks that the Director, DceneDirector and Role have their methods called in the correct order
# when a scene is loaded.
#
class LoadEvents(Test) :

    def __init__(self) :
        super(LoadEvents,self).__init__()
        self.done = False
        self.events = ""
        self.event("sceneDirector.__init__")
        self.directorEvent()

    def loading(self,scene) :
        game.director = TestDirector()
        self.event( "sceneDirector.loading" )
        
    def onLoaded(self) :
        self.event( "sceneDirector.onLoaded" )
                    
    def onActivate(self) :
        self.directorEvent()
        self.event( "sceneDirector.onActivate" )
        
    def tick(self) :
        if self.done :
            self.directorEvent()
            print self.events
            self.compare( "loadEvents", "sceneDirector.__init__\nsceneDirector.loading\ndirector.__init__\nrole.onBirth\nrole.onActivate\nsceneDirector.onLoaded\nrole.onSceneCreated\nsceneDirector.onActivate\ndirector.onStartedScene,tick\nsceneDirector.tick\nrole.tick\ndirector.tick\n", self.events )
            game.setDirector( PlainDirector() )            
            self.endTest()
            return

        self.directorEvent()
        self.event( "sceneDirector.tick" )

        self.done = True

    def directorEvent(self) :
        if hasattr( game.director, "events" ) and game.director.events is not None :
            self.event( "director." + game.director.events )
            game.director.events = None
            
    def event(self, text) :
        self.events += str(text) + "\n"

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


