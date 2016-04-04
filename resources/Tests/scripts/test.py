from common import * #@UnusedWildImport


properties = ArrayList()
properties.add( StringProperty( "nextScene" ) )

class Test(PlainSceneDirector) :

    def __init__(self) :
        self.nextScene = "passed"
        self.ok = True
        self.inputExit = Input.find( "exit" )

    def onKeyDown(self, ke) :
        if self.inputExit.matches( ke ) :
            game.startScene( "menu" )
    
    def compare( self, description, expected, value ) :
        same = expected == value
        # If they are numbers, then allow them to be a little off, because rounding errors are to be expected.
        try :
            diff = value - expected
            same = diff > -0.000001 and diff < 0.000001
        except :
            pass
    
        if not same :        
            print "Test failed", description, "tick", "Expected", expected, "found", value
            self.ok = False


    def endTest(self) :
        if self.ok :
            print game.sceneName, ": Passed"
            game.startScene( self.nextScene )
        else :
            print game.sceneName, ": Failed"
            game.mergeScene( "failed" )
            game.sceneDirector = PlainSceneDirector()
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

