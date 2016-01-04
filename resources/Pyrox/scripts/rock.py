from common import *

import gridRole
from faller import Faller

properties = ArrayList()

class Rock(Faller) :

    def onBirth( self ) :
        super(Rock,self).onBirth()
        
        self.weight = 4

        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )
        
        self.addTag( "explosionTrigger" )
        self.addTag( "deadly" );


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


