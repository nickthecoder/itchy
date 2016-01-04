from common import *

from collectable import Collectable

properties = ArrayList()
properties.add( StringProperty( "keyName" ) )

class Safe(Collectable) :

    def __init__(self) :
        super(Safe,self).__init__()
        self.keyName = "key"

    def onBirth(self) :
        super(Safe, self).onBirth()
        self.addTag("safe-" + self.keyName )
        self.removeTag("soft") # Not collectable until the key has been collected.
        
    def unlock(self) :
        self.addTag("soft")

        # Make myself rounded while I'm opening - the act of opening the safe will
        # shake it a little, letting rocks etc roll off of it.
        self.addTag( "roundedNE" )
        self.addTag( "roundedSE" )
        self.addTag( "roundedSW" )
        self.addTag( "roundedNW" )
 
        self.event("open", "open")
        
    def onMessage( self, message ) :
        if message == "open" :
            self.removeTag( "roundedNE" )
            self.removeTag( "roundedSE" )
            self.removeTag( "roundedSW" )
            self.removeTag( "roundedNW" )
        
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


