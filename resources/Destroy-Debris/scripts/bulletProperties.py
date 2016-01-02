from common import *

properties = ArrayList()

properties.add( DoubleProperty("impulse").hint("recoils the ship") )
properties.add( IntegerProperty("strength") )
properties.add( DoubleProperty("firePeriod") )
properties.add( DoubleProperty("explosiveness").hint("The momentum added to the fragments") )
 
class BulletProperties(CostumeProperties) :

    def __init__(self) :
        self.impulse = 1
        self.strength = 1
        self.firePeriod = 0.2
        self.explosiveness = 1


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )

