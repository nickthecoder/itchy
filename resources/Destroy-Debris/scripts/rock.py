from common import * #@UnusedWildImport

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
costumeProperties.add( IntegerProperty("offsetX") )
costumeProperties.add( IntegerProperty("offsetY") )

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
        self.actor.appearance.direction += self.rotationSpeed

    
    
    def shot(self, bullet) :
        # Small bullets have NO effect on strong rocks.
        strength = bullet.actor.costume.costumeFeatures.strength

        if strength < self.actor.costume.costumeFeatures.strength :
            self.event("ricochet")
            return
    
        self.hits += strength
        # Have we hit the rock enough times?
        if self.hits < self.actor.costume.costumeFeatures.hitsRequired :
            self.event("hit")
            return
    
        self.getActor().event("explode")
        game.getDirector().addPoints(self.actor.costume.costumeFeatures.points)
        
        ExplosionBuilder(self.getActor()) \
            .spread( bullet.getActor().getHeading() - 120, bullet.getActor().getHeading() + 120 ).randomSpread() \
            .speed(5,3,0,0).fade(3).distance(40) \
            .rotate(True).pose("fragment").projectiles(8) \
            .create()

        sum_dx = 0
        sum_dy = 0
        pieces = self.actor.costume.costumeFeatures.pieces
        for i in range( 0, pieces ) :
            actor = self.getActor().createCompanion("fragment-"+`i + 1`)
            ox = actor.costume.costumeFeatures.offsetX
            oy = actor.costume.costumeFeatures.offsetY
            actor.direction = self.actor.direction
            actor.moveForwards( ox, oy )
            role = actor.getRole()
            
            explosiveness = bullet.actor.costume.costumeFeatures.explosiveness
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
            


        game.getSceneDirector().addRocks(-1)
        self.removeTag("shootable")
        self.getActor().deathEvent("explode")
    
    
    def createCostumeFeatures( self, costume ) :
        return RockFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class RockFeatures(CostumeFeatures) :

    def __init__(self, costume) :
        self.pieces = 0
        self.points = 1    
        self.strength = 1    
        self.hitsRequired = 1  
        self.offsetX = 0
        self.offsetY = 0  

    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

