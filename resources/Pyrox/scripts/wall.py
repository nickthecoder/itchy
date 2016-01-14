from common import *

from gridRole import GridRole
import roundProperties
from roundProperties import RoundProperties

properties = ArrayList()

costumeProperties = ArrayList()
costumeProperties.addAll( roundProperties.costumeProperties )
costumeProperties.add( BooleanProperty( "canExplode" ).label( "Can Explode" ) )


class Wall(GridRole) :

    def onBirth( self ) :
        super(Wall,self).onBirth()
        
        self.costumeProperties.update(self)


    def createCostumeProperties(self,costume) :
        return WallProperties(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class WallProperties(RoundProperties) :

    def __init__(self,costume) :
        super(WallProperties,self).__init__(costume)
        self.canExplode = True

    def update(self, role) :
        super(WallProperties,self).update(role)
        
        role.tag( "explodable", self.canExplode )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

