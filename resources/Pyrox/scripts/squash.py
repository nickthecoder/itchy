from common import *

from gridRole import GridRole
from roundProperties import RoundProperties
import roundProperties

properties = ArrayList()

costumeProperties = ArrayList()
costumeProperties.addAll( roundProperties.costumeProperties )
costumeProperties.add( BooleanProperty( "squashN" ) )
costumeProperties.add( BooleanProperty( "squashE" ) )
costumeProperties.add( BooleanProperty( "squashS" ) )
costumeProperties.add( BooleanProperty( "squashW" ) )
costumeProperties.add( BooleanProperty( "permanent" ) )

class Squash(GridRole) :
        
    def onBirth(self):
        super(Squash,self).onBirth()
        self.costumeProperties.update(self)


    def onPlacedOnGrid(self) :
        if self.permanent :
            self.makeAlternateOccupant()


    def onInvaded( self, invader ) :
        pass

    def onHalfInvaded(self,invader):
        if not self.permanent :
            self.removeFromGrid()
            self.event("fade")
    
    def shove( self, pusher, dx, dy, speed ) :
        pass
        
    def createCostumeProperties(self,costume) :
        return SquashProperties(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class SquashProperties(RoundProperties) :

    def __init__(self,costume) :
        super(SquashProperties,self).__init__(costume)
        self.squashN = False
        self.squashE = False
        self.squashS = False
        self.squashW = False
        self.permanent = False
        
    def update( self, role ) :
        super(SquashProperties,self).update(role)
        
        role.tag( "squashN", self.squashN )
        role.tag( "squashE", self.squashE )
        role.tag( "squashS", self.squashS )
        role.tag( "squashW", self.squashW )
        role.permanent = self.permanent
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties


