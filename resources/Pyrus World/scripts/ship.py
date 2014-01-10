import math

from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import OnionSkin
from uk.co.nickthecoder.itchy.role import Explosion
from uk.co.nickthecoder.itchy.extras import Timer
from uk.co.nickthecoder.itchy.extras import Fragment

from uk.co.nickthecoder.jame.event import Keys

from java.util import ArrayList
from moving import Moving

properties = ArrayList()

class Ship(Moving) :

    def __init__(self) :
        Moving.__init__(self)
        self.lifeIcon = []

    def onBirth(self) :
        OnionSkin( self.getActor() ).alpha(128).every(5).fade(3).createActor();

        self.rotationSpeed = self.getActor().getCostume().getProperties().rotationSpeed;
        self.thrust = self.getActor().getCostume().getProperties().thrust;
        self.firePeriod = self.getActor().getCostume().getProperties().firePeriod;
        
        self.fireTimer = Timer.createTimerSeconds(self.firePeriod);        

        # Cut the ship into 3 large pieces, and call these poses "part"
        Fragment().actor(self.getActor()).pieces(3).createPoses("part");
        # Cut the ship again, this time into 10 pieces, and call these poses "fragment".
        Fragment().actor(self.getActor()).pieces(10).createPoses("fragment");
        # These are use together when the ship explodes in the "die" method.

        Itchy.getGame().getSceneDirector().ship = self;
        
        Explosion(self.getActor()) \
            .companion("warp").eventName("default").distance(30,-80).spread(0,360).spread(0,360).randomSpread(False) \
            .speed(0,0).projectiles(40) \
            .createActor();

        for i in range( 0, Itchy.getGame().getDirector().lives ) :
            actor = self.getActor().createCompanion("life")
            Itchy.getGame().getDirector().hudStage.add(actor)
            actor.moveTo( 30 + i * 40 , 560 )
            if Itchy.getGame().getSceneName() == "1" :
                actor.event("appear");
                
            self.lifeIcon.append(actor);

    def warp(self) :
    
        for i in range( 0, 3 ) :
            Explosion(self.getActor()) \
                .companion("warp") \
                .spread(i*120, 360 + i*120).vx(self.vx).vy(self.vy).distance(100) \
                .speed(-6,0).projectiles(20).projectilesPerTick(1).randomSpread(False).alpha(0).fade(-3) \
                .createActor()

        self.getActor().deathEvent("fade")

    def tick(self) :

        if Itchy.isKeyDown(Keys.LEFT) :
            self.getActor().adjustDirection( self.rotationSpeed )
        if Itchy.isKeyDown(Keys.RIGHT) :
            self.getActor().adjustDirection( -self.rotationSpeed );

        if Itchy.isKeyDown(Keys.UP) :
            theta = self.getActor().getHeadingRadians()
            self.vx += math.cos(theta) * self.thrust
            self.vy += math.sin(theta) * self.thrust
            heading = self.getActor().getDirection()
            
            Explosion(self.getActor()) \
                .projectiles(4).follow().projectilesPerTick(1) \
                .spread(heading+160, heading+200).distance(40) \
                .randomSpread().speed(1,2,0,0).fade(3).eventName("spark") \
                .createActor()

        if Itchy.isKeyDown(Keys.SPACE) :
            if self.fireTimer.isFinished() :
                self.fire()
                self.fireTimer.reset()

        # Move and wrap from one edge of the world to the opposite.
        Moving.tick(self)
        
        if not self.getCollisionStrategy().collisions(self.getActor(),"deadly").isEmpty() :
            self.explode();

        # For debugging.
        if Itchy.isKeyDown(Keys.x) :
            Itchy.getGame().getSceneDirector().addRocks(-1);
    
    def explode( self ) :
       
       # Use the "fragment" and "part" poses created in onBirth to explode the ship in all directions.
       # The large "part" pieces move slowly, and the smaller "fragment" pieces move quickly.
        Explosion(self.getActor()) \
            .speed(0.5,0,1,0).fade(3).spin(-1,1).rotate(True).eventName("part").projectiles(4).createActor()
        Explosion(self.getActor()) \
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
        actor = self.actor.createCompanion("bullet")

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

