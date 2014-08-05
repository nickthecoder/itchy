from uk.co.nickthecoder.itchy import CostumeProperties
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import IntegerProperty

properties = ArrayList()
properties.add( IntegerProperty( "width" ) )
properties.add( IntegerProperty( "height" ) )
properties.add( IntegerProperty( "talkX" ) )
properties.add( IntegerProperty( "talkY" ) )

class BigProperties(CostumeProperties) :

    def __init__(self) :
        self.width = 1
        self.height = 1
        self.talkX = 70
        self.talkY = 10

    def createParts(self, big) :
        for x in range(0,self.width) :
            for y in range(0,self.height) :
                if x != 0 or y != 0 :
                    big.createPart(x, y)

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


