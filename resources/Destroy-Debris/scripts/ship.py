from common import *

from moving import Moving

properties = ArrayList()
            
costumeProperties = ArrayList()
costumeProperties.add( DoubleProperty("rotationSpeed") )
costumeProperties.add( DoubleProperty("thrust") )
  
game = Itchy.getGame()

class Ship(Moving) :

    def __init__(self) :
        Moving.__init__(self)
        self.lifeIcon = []
        self.bulletName = "bullet-1"


    def onBirth(self) :
    
        self.inputLeft = Input.find("left")
        self.inputRight = Input.find("right")
        self.inputThrust = Input.find("thrust")
        self.inputFire = Input.find("fire")
        self.inputCheat = Input.find("cheat")
    
        self.inputWeapon1 = Input.find("weapon-1")
        self.inputWeapon2 = Input.find("weapon-2")
    
        OnionSkinBuilder( self.getActor() ) \
            .alpha(128).every(5).fade(3).create()

        self.rotationSpeed = self.getActor().getCostume().getCostumeProperties().rotationSpeed
        self.thrust = self.getActor().getCostume().getCostumeProperties().thrust
        self.fireTimer = None
        
        # Cut the ship into 3 large pieces, and call these poses "part"
        Fragment().actor(self.getActor()).pieces(3).createPoses("part")
        # Cut the ship again, this time into 10 pieces, and call these poses "fragment".
        Fragment().actor(self.getActor()).pieces(10).createPoses("fragment")
        # These are use together when the ship explodes in the "die" method.

        game.getSceneDirector().ship = self
        
        ExplosionBuilder(self.getActor()) \
            .companion("warp").eventName("default").distance(30,-80).spread(0,360).spread(0,360).randomSpread(False) \
            .speed(0,0).projectiles(40) \
            .create()
        
        for i in range( 0, game.getDirector().lives ) :
            actor = self.getActor().createCompanion("life")
            game.getDirector().hudStage.add(actor)
            actor.moveTo( 30 + i * 40 , 560 )
            if game.getSceneName() == "1" :
                actor.event("appear")
                
            self.lifeIcon.append(actor)


    def warp(self) :
    
        for i in range( 0, 3 ) :
            ExplosionBuilder(self.getActor()) \
                .companion("warp") \
                .spread(i*120, 360 + i*120).vx(self.vx).vy(self.vy).distance(100) \
                .speed(-6,0).projectiles(20).projectilesPerTick(1).randomSpread(False).alpha(0).fade(-3) \
                .create()

        self.getActor().deathEvent("fade")


    def tick(self) :
            
        if self.inputLeft.pressed() :
            self.getActor().adjustDirection( self.rotationSpeed )
            
        if self.inputRight.pressed() :
            self.getActor().adjustDirection( -self.rotationSpeed )

        if self.inputThrust.pressed() :
            theta = self.getActor().getHeadingRadians()
            self.vx += math.cos(theta) * self.thrust
            self.vy += math.sin(theta) * self.thrust
            heading = self.getActor().getDirection()
            
            ExplosionBuilder(self.getActor()) \
                .projectiles(4).follow().projectilesPerTick(1) \
                .spread(heading+160, heading+200).distance(40) \
                .randomSpread().speed(1,2,0,0).fade(3).eventName("spark") \
                .create()

        if self.fireTimer == None or self.fireTimer.isFinished() :
        
            if self.inputFire.pressed() :
                if self.fireTimer is None :
                    firePeriod = self.getActor().getCostume().getCompanion(self.bulletName).getCostumeProperties().firePeriod
                    self.fireTimer = Timer.createTimerSeconds(firePeriod)

                self.fire()
                self.fireTimer.reset()

            if self.inputWeapon1.pressed() :
                self.bulletName = "bullet-1"
                self.fireTimer = None

            if self.inputWeapon2.pressed() :
                self.bulletName = "bullet-2"
                self.fireTimer = None

        # Move and wrap from one edge of the world to the opposite.
        Moving.tick(self)
        
        if self.collided("deadly") :
            self.explode()

        # For debugging.
        if self.inputCheat.pressed() :
            game.getSceneDirector().addRocks(-1)


    def explode( self ) :
       
       # Use the "fragment" and "part" poses created in onBirth to explode the ship in all directions.
       # The large "part" pieces move slowly, and the smaller "fragment" pieces move quickly.
        ExplosionBuilder(self.getActor()) \
            .speed(0.5,0,1,0).fade(3).spin(-1,1).rotate(True).eventName("part").projectiles(4).create()
            
        ExplosionBuilder(self.getActor()) \
            .speed(1.5,0,4,0).fade(3).spin(-1,1).rotate(True).eventName("fragment").projectiles(20).create()
        
        game.getDirector().lives -= 1

        self.lifeIcon[game.getDirector().lives].event("disappear")
        self.actor.deathEvent("explode", "exploded")


    def onMessage(self, message ) :
        if message == "exploded" :
            if game.getDirector().lives > 0 :
                game.startScene(game.getSceneName())
            else :
                game.getDirector().showFancyMouse()
                game.loadScene("gameOver", True)


    def fire(self) :
        actor = self.actor.createCompanion(self.bulletName)

        actor.setDirection( self.getActor().getHeading() )
        actor.moveTo( self.getActor() )
        actor.moveForwards(40)

        impulse = actor.getCostume().getCostumeProperties().impulse
        theta = self.getActor().getHeadingRadians()
        self.vx -= math.cos(theta) * impulse
        self.vy -= math.sin(theta) * impulse

    def createCostumeProperties( self ) :
        return ShipProperties()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class ShipProperties(CostumeProperties) :

    def __init__(self) :
        self.rotationSpeed = 2
        self.thrust = 1    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

