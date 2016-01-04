from common import *

from gridRole import GridRole

properties = ArrayList()

class Soft(GridRole) :
    
    def onAttach( self ) :
        super(Soft,self).onAttach()
        self.addTag("soft")
        self.addTag("enemySoft")

    def onPlacedOnGrid( self ) :
        self.makeAlternateOccupant()

    def onHalfInvaded( self, invader ) :
        if not invader.hasTag("bee") :
            super(Soft,self).onHalfInvaded(invader)
            self.explode()
        
    def onInvaded( self, invader ) :
        pass
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


