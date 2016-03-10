from common import * #@UnusedWildImport

class TestDirector(AbstractDirector) :

    def __init__(self) :
        self.events = None
        self.event("__init__")

    def onStartingScene(self, sceneName) :
        self.event("onStartingScene")

    def onStartedScene(self) :
        self.event("onStartedScene")
    
    def message(self, message) :
        self.event( "message " + message )

    def tick(self) :
        self.event("tick")

    def event(self,text) :
        if self.events is None :
            self.events = text
        else :
            self.events += "," + text

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


