from common import *

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
        role = grid.getGridRole(square.x,square.y)

        if role is None :
            return self.test( "class", self.expectedClass, "Empty" )
        else :
            return self.test( "class", self.expectedClass, role.__class__.__name__ )
                
    def test( self, waffle, expected, found ) :
    
        if expected == found :
            self.actor.event("pass")
            return True
        else :
            self.actor.event("fail")
            print waffle, ". Expected : ", expected, " found : ", found
            return False


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


