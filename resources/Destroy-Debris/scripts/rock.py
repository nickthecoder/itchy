from common import *

from moving import Moving

properties = ArrayList()
properties.add( DoubleProperty("strength") )
properties.add( DoubleProperty("rotationSpeed").hint("Degrees per Tick)") )
properties.add( DoubleProperty("vx").label("X Velocity") )
properties.add( DoubleProperty("vy").label("Y Velocity") )


costumeProperties = ArrayList()
costumeProperties.add( IntegerProperty("pieces") )
costumeProperties.add( IntegerProperty("points") )
costumeProperties.add( IntegerProperty("strength") )
costumeProperties.add( IntegerProperty("hitsRequired") )
 
game = Itchy.getGame()

class Rock(Moving) :

    def __init__(self) :
        Moving.__init__(self)
        self.strength = 1
        self.rotationSpeed = 0
        self.hits = 0
        
        
    def onBirth(self):
        game.getSceneDirector().addRocks(1)


    def onAttach(self):
        Moving.onAttach(self)
        self.addTag("shootable")
        self.addTag("deadly")

        
    def tick(self):
        Moving.tick(self)
        self.getActor().getAppearance().adjustDirection(self.rotationSpeed)

    
    def shot(self, bullet) :
    	# Small bullets have NO effect on strong rocks.
    	strength = bullet.getActor().getCostume().getCostumeProperties().strength

    	if strength < self.getActor().getCostume().getCostumeProperties().strength :
    		self.getActor().event("ricochet")
    		return
    	
    	self.hits += strength
    	# Have we hit the rock enough times?
    	if self.hits < self.getActor().getCostume().getCostumeProperties().hitsRequired :
    		self.getActor().event("hit")
    		return
    	
		self.getActor().event("explode")
    	game.getDirector().addPoints(self.getActor().getCostume().getCostumeProperties().points)
    	
        ExplosionBuilder(self.getActor()) \
            .spread( bullet.getActor().getHeading() - 120, bullet.getActor().getHeading() + 120 ).randomSpread() \
            .speed(5,3,0,0).fade(3).distance(40) \
            .rotate(True).pose("fragment").projectiles(8) \
            .create()

        sum_dx = 0
        sum_dy = 0
        pieces = self.getActor().getCostume().getCostumeProperties().pieces
        for i in range( 0, pieces ) :
            actor = self.getActor().createCompanion("fragment-"+`i + 1`)
            role = actor.getRole()
            
            explosiveness = bullet.getActor().getCostume().getCostumeProperties().explosiveness
            role.rotationSpeed = self.rotationSpeed + Util.randomBetween( -explosiveness, explosiveness )

            if i == pieces -1 :
                dx = -sum_dx
                dy = -sum_dy
            else :
                dx = Util.randomBetween( -explosiveness, explosiveness )
                dy = Util.randomBetween( -explosiveness, explosiveness )
                sum_dx += dx
                sum_dy += dy
            role.vx = self.vx + dx
            role.vy = self.vy + dy
            
            actor.setDirection( self.getActor().getAppearance().getDirection() )

        game.getSceneDirector().addRocks(-1)
        self.getActor().deathEvent("explode")
        self.removeTag("shootable")
    
    
    def createCostumeProperties( self ) :
        return RockProperties()


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class RockProperties(CostumeProperties) :

    def __init__(self) :
        self.pieces = 0
        self.points = 1    
        self.strength = 1    
        self.hitsRequired = 1    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties
