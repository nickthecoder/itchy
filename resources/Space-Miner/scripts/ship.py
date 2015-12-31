import math

from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import OnionSkinBuilder
from uk.co.nickthecoder.itchy.role import ExplosionBuilder
from uk.co.nickthecoder.itchy.extras import Timer
from uk.co.nickthecoder.itchy.extras import Fragment


from java.util import ArrayList
from moving import Moving

properties = ArrayList()

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
            .alpha(128).every(5).fade(3).createActor();

        self.rotationSpeed = self.getActor().getCostume().getProperties().rotationSpeed;
        self.thrust = self.getActor().getCostume().getProperties().thrust;
        self.fireTimer = None
        
        # Cut the ship into 3 large pieces, and call these poses "part"
        Fragment().actor(self.getActor()).pieces(3).createPoses("part");
        # Cut the ship again, this time into 10 pieces, and call these poses "fragment".
        Fragment().actor(self.getActor()).pieces(10).createPoses("fragment");
        # These are use together when the ship explodes in the "die" method.

        Itchy.getGame().getSceneDirector().ship = self;
        
        ExplosionBuilder(self.getActor()) \
            .companion("warp").eventName("default").distance(30,-80).spread(0,360).spread(0,360).randomSpread(False) \
            .speed(0,0).projectiles(40) \
            .createActor();

        print "Lives : ", Itchy.getGame().getDirector().lives
        
        for i in range( 0, Itchy.getGame().getDirector().lives ) :
            actor = self.getActor().createCompanion("life")
            Itchy.getGame().getDirector().hudStage.add(actor)
            actor.moveTo( 30 + i * 40 , 560 )
            if Itchy.getGame().getSceneName() == "1" :
                actor.event("appear");
                
            self.lifeIcon.append(actor);

    def warp(self) :
    
        for i in range( 0, 3 ) :
            ExplosionBuilder(self.getActor()) \
                .companion("warp") \
                .spread(i*120, 360 + i*120).vx(self.vx).vy(self.vy).distance(100) \
                .speed(-6,0).projectiles(20).projectilesPerTick(1).randomSpread(False).alpha(0).fade(-3) \
                .createActor()

        self.getActor().deathEvent("fade")

    def tick(self) :

            
        if self.inputLeft.pressed() :
            self.getActor().adjustDirection( self.rotationSpeed )
            
        if self.inputRight.pressed() :
            self.getActor().adjustDirection( -self.rotationSpeed );

        if self.inputThrust.pressed() :
            theta = self.getActor().getHeadingRadians()
            self.vx += math.cos(theta) * self.thrust
            self.vy += math.sin(theta) * self.thrust
            heading = self.getActor().getDirection()
            
            ExplosionBuilder(self.getActor()) \
                .projectiles(4).follow().projectilesPerTick(1) \
                .spread(heading+160, heading+200).distance(40) \
                .randomSpread().speed(1,2,0,0).fade(3).eventName("spark") \
                .createActor()

        if self.fireTimer == None or self.fireTimer.isFinished() :
        
            if self.inputFire.pressed() :
                if self.fireTimer is None :
                    firePeriod = self.getActor().getCostume().getCompanion(self.bulletName).getProperties().firePeriod;
                    self.fireTimer = Timer.createTimerSeconds(firePeriod);

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
            self.explode();

        # For debugging.
        if self.inputCheat.pressed() :
            Itchy.getGame().getSceneDirector().addRocks(-1);
    
    def explode( self ) :
       
       # Use the "fragment" and "part" poses created in onBirth to explode the ship in all directions.
       # The large "part" pieces move slowly, and the smaller "fragment" pieces move quickly.
        ExplosionBuilder(self.getActor()) \
            .speed(0.5,0,1,0).fade(3).spin(-1,1).rotate(True).eventName("part").projectiles(4).createActor()
            
        ExplosionBuilder(self.getActor()) \
            .speed(1.5,0,4,0).fade(3).spin(-1,1).rotate(True).eventName("fragment").projectiles(20).createActor()
        
        Itchy.getGame().getDirector().lives -= 1

        self.lifeIcon[Itchy.getGame().getDirector().lives].event("disappear")
        self.actor.deathEvent("explode", "exploded")
    
    def onMessage(self, message ) :
        if message == "exploded" :
            if Itchy.getGame().getDirector().lives > 0 :
                Itchy.getGame().startScene(Itchy.getGame().getSceneName())
            else :
                Itchy.getGame().getDirector().showFancyMouse()
                Itchy.getGame().loadScene("gameOver", True)
    
    def fire(self) :
        actor = self.actor.createCompanion(self.bulletName)

        actor.setDirection( self.getActor().getHeading() )
        actor.moveTo( self.getActor() )
        actor.moveForwards(40)

        impulse = actor.getCostume().getProperties().impulse;
        theta = self.getActor().getHeadingRadians()
        self.vx -= math.cos(theta) * impulse;
        self.vy -= math.sin(theta) * impulse;
    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )

