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

        self.rotationSpeed = self.costumeFeatures.rotationSpeed
        self.thrust = self.costumeFeatures.thrust
        self.fireTimer = None
        
        # Cut the ship into 3 large pieces, and call these poses "part"
        Fragments().pieces(3).createPoses(self.getActor(), "part")
        # Cut the ship again, this time into 10 pieces, and call these poses "fragment".
        Fragments().pieces(10).createPoses(self.getActor())
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
                    firePeriod = self.actor.costume.getCompanion(self.bulletName).costumeFeatures.firePeriod
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
    
        ExplosionBuilder(self.actor) \
            .fragments( self.costumeFeatures.fragments ) \
            .speed(1.0,0,1.5,0).fade(3).spin(-1,1).vx(self.vx/2).vy(self.vy/2) \
            .create()
        
        game.director.lives -= 1

        self.lifeIcon[game.director.lives].event("disappear")
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

        impulse = actor.costume.costumeFeatures.impulse
        theta = self.getActor().getHeadingRadians()
        self.vx -= math.cos(theta) * impulse
        self.vy -= math.sin(theta) * impulse

    def createCostumeFeatures( self, costume ) :
        return ShipFeatures(costume)

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class ShipFeatures(CostumeFeatures) :

    def __init__(self,costume) :
        super(ShipFeatures,self).__init__(costume)
        self.rotationSpeed = 2
        self.thrust = 1   
        pose = costume.getPose("default")
        if pose is not None :
            self.fragments = Fragments().create(pose)
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

