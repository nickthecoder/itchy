from common import *

properties = ArrayList()
properties.add( IntegerProperty("pieces") )
properties.add( IntegerProperty("points") )
properties.add( IntegerProperty("strength") )
properties.add( IntegerProperty("hitsRequired") )
 
class RockProperties(CostumeProperties) :

    def __init__(self) :
    	self.pieces = 0
    	self.points = 10
    	self.strength = 0
    	self.hitsRequired = 1


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )

