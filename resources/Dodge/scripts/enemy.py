from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "vx" ).aliases("dx") )
properties.add( DoubleProperty( "vy" ).aliases("dy") )
properties.add( DoubleProperty( "rotation" ) )

featureProperties  = ArrayList()
featureProperties.add( StringProperty( "clan" ) )

class Enemy(AbstractRole) :

    def __init__(self) :
        self.vx = 0
        self.vy = 0
        self.rotation = 0
        self.addTag("enemy")

        
    def onBirth( self ) :
        self.addTag( self.costumeFeatures.clan )
    
    def tick(self):
        
        self.actor.x += self.vx
        self.actor.y += self.vy
        self.actor.direction += self.rotation



    def createCostumeFeatures(self,costume) :
        return EnemyFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class EnemyFeatures(CostumeFeatures) :

    def __init__(self, costume) :
        super(EnemyFeatures,self).__init__(costume)
        self.clan = "none"

    # Boiler plate code - no need to change this
    def getProperties(self):
        return featureProperties

