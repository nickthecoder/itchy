from common import *

from gridRole import GridRole
import roundFeatures
from roundFeatures import RoundFeatures

properties = ArrayList()

costumeProperties = ArrayList()
costumeProperties.addAll( roundFeatures.costumeProperties )
costumeProperties.add( BooleanProperty( "canExplode" ).label( "Can Explode" ) )


class Wall(GridRole) :

    def onBirth( self ) :
        super(Wall,self).onBirth()
        
        self.costumeFeatures.update(self)


    def createCostumeFeatures(self,costume) :
        return WallFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class WallFeatures(RoundFeatures) :

    def __init__(self,costume) :
        super(WallFeatures,self).__init__(costume)
        self.canExplode = True

    def update(self, role) :
        super(WallFeatures,self).update(role)
        
        role.tag( "explodable", self.canExplode )


    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

