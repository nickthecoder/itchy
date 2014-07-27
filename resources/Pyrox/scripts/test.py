from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty

properties = ArrayList()
properties.add( StringProperty( "expectedClass" ) )

class Test(AbstractRole) :

    def __init__(self) :
        self.expectedClass = ""
                
    def onBirth(self) :
        self.addTag("test")
        
    def run(self) :
    
        grid = Itchy.getGame().sceneDirector.grid
        square = grid.getSquareByPixel( self.actor.getX(), self.actor.getY() )
        print "Test @ ", self.actor.getX(), self.actor.getY()
        role = square.occupant

        return self.test( "class", self.expectedClass, role.__class__.__name__ )
                
    def test( self, waffle, expected, found ) :
    
        if expected == found :
            self.actor.event("pass")
            return True
        else :
            self.actor.event("fail")
            print waffle, ". Expected : ", expected, " found : ", found
            return False


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


