from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import Actor
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import Explosion

from uk.co.nickthecoder.itchy.property import BooleanProperty

from java.util import ArrayList

from gridRole import GridRole
import gridRole
from big import Big

properties = ArrayList()
properties.add( BooleanProperty( "awake" ) )

# The main character in the game, i.e. the one that the user is in controll of.
# There is usually just one Player in a Scene, but it is possible to have more, in which case,
# Level controls which Player is awake, and which are sent to sleep.
#
# The views are centered on the (awake) Player, but this can be offset somewhat using Home/End/PgUp/PgDn.
#
class Player(Big) :

    def __init__(self) :
        Big.__init__(self)

        self.awake = True

        self.inputLeft = Input.find("left")
        self.inputRight = Input.find("right")
        self.inputUp = Input.find("up")
        self.inputDown = Input.find("down")
        
        self.inputScrollLeft = Input.find("scrollLeft")
        self.inputScrollRight = Input.find("scrollRight")
        self.inputScrollUp = Input.find("scrollUp")
        self.inputScrollDown = Input.find("scrollDown")
        self.inputScrollReset = Input.find("scrollReset")
                
        self.scrollOffsetX = 0
        self.scrollOffsetY = 0
        self.maxScrollX = 400
        self.maxScrollY = 300

        self.scrollResetting = False
        self.scrollSpeed = 1
        self.defaultScrollSpeed = 1
        
        self.sleepyZ = None 
        

    def onBirth( self ) :
        Big.onBirth(self)

        cp = self.actor.costume.properties
        self.talkX = cp.talkX
        self.talkY = cp.talkY
        
        self.speed = 6
        self.addTag("player")
        self.addTag("digger") # Allows me to dig hard soil. See class Hard

    def onPlacedOnGrid(self) :
        Big.onPlacedOnGrid(self)

        # My costume properties should be BigProperties (or something compatable).
        self.actor.costume.properties.createParts( self )
        self.calculateLeadingEdges()

        self.allAddTag("hittable")
        
            
    # Called by Level to let me find the "warp" of the scene that's just been completed.
    def getReady( self, wake ) :
    
        if wake :
            for warp in Itchy.getGame().findRoleByTag("warp") :
                director = Itchy.getGame().getDirector()
                if director.previousSceneName == warp.scene :
                    x = warp.getActor().getX() + warp.exitX * director.squareSize
                    y = warp.getActor().getY() + warp.exitY * director.squareSize
                    self.moveTo( x, y )
                    break
                    
        else :
            self.sleep()
        
    def tick(self) :
        pass

    # The player's tick is special - it is called before all other objects on the grid.
    # This is so that it is easier to predict what will happen objects near us.
    # For example, we don't want objects to the left to act different to those to the right
    # just because our tick happens before one and after the other.
    def playerTick( self ) :

        
        if self.scrollResetting :
            self.resetScroll()
        elif self.inputScrollReset.pressed() :
            self.scrollResetting = True
        elif self.inputScrollLeft.pressed() :
            self.scroll( -1, 0 )
        elif self.inputScrollRight.pressed() :            
            self.scroll( 1, 0 )
        elif self.inputScrollUp.pressed() :            
            self.scroll( 0, 1 )
        elif self.inputScrollDown.pressed() :            
            self.scroll( 0, -1 )
        else :
            self.scrollSpeed = self.defaultScrollSpeed
            
        if self.isMoving() or self.square is None :
            pass

        else :
            
            self.movements()

        Big.tick(self)
        
        tx = self.actor.getX() + self.scrollOffsetX
        ty = self.actor.getY() + self.scrollOffsetY
        
        Itchy.getGame().getDirector().centerOn( tx, ty )


    def movements(self) :

        if self.inputLeft.pressed() and self.attemptToMove( -1, 0 ) :
            return
        
        elif self.inputRight.pressed() and self.attemptToMove( 1, 0 ) :
            return
            
        elif self.inputUp.pressed() and self.attemptToMove( 0, 1 ) :
            return
            
        elif self.inputDown.pressed() and self.attemptToMove( 0, -1 ) :
            return
            

    
    def sleep( self ) :
        self.event("sleep")
        self.awake = False
        x = self.talkX - 40
        y = self.talkY + 20
        self.sleepyZ = Explosion(self).pose("z") \
            .offsetForwards( x,x ).offsetSidewards( y,y ) \
            .vy(0.6, 1.2).vx(0.2,0.3).gravity(-0.01) \
            .fade( 2 ) \
            .projectilesPerTick(1).slow(50).forever() \
            .createActor()
        
    def wake( self ) :
        self.killZs()
        self.event("wake")
        self.awake = True
        
    def killZs( self ) :
        if self.sleepyZ :
            self.sleepyZ.kill()
            self.sleepyZ = None
            
    def resetScroll( self ) :
        
        if self.scrollOffsetX < 0 :
            self.scrollOffsetX += self.scrollSpeed
            if self.scrollOffsetX > 0 :
                self.scrollOffsetX = 0
                
        elif self.scrollOffsetX > 0 :                
            self.scrollOffsetX -= self.scrollSpeed
            if self.scrollOffsetX < 0 :
                self.scrollOffsetX = 0
        
        if self.scrollOffsetY < 0 :
            self.scrollOffsetY += self.scrollSpeed
            if self.scrollOffsetY > 0 :
                self.scrollOffsetY = 0
                
        elif self.scrollOffsetY > 0 :                
            self.scrollOffsetY -= self.scrollSpeed
            if self.scrollOffsetY < 0 :
                self.scrollOffsetY = 0
    
        if self.scrollOffsetX == 0 and self.scrollOffsetY == 0 :
            self.scrollResetting = False
        else :
            self.scrollSpeed += 0.5
            
    
    def scroll( self, dx, dy ) :
    
        self.scrollOffsetX += dx * self.scrollSpeed
        self.scrollOffsetY += dy * self.scrollSpeed
        
        self.scrollSpeed += 0.5
        
        if self.scrollOffsetX < -self.maxScrollX :
            self.scrollOffsetX = -self.maxScrollX
        if self.scrollOffsetX > self.maxScrollX :
            self.scrollOffsetX = self.maxScrollX
    
        if self.scrollOffsetY < -self.maxScrollY :
            self.scrollOffsetY = -self.maxScrollY
        if self.scrollOffsetY > self.maxScrollY :
            self.scrollOffsetY = self.maxScrollY
    
    
    
    def attemptToMove( self, dx, dy ) :

        squashTags = ["soft","squash" + gridRole.getDirectionAbreviation(dx,dy)]        
        if self.canShoveNeigbours( dx, dy, self.speed, 4, squashTags ) :
            self.shoveNeighbours( dx, dy, self.speed, squashTags )
            self.move(dx, dy)
            return True
        
        return False
       
    def move( self, dx, dy, speed=None ) :
        Big.move(self,dx, dy, speed )
        if dy == 0 :
            self.actor.event( "move-" + ("L" if dx == -1 else "R" ) )
        else :
            self.actor.event( "move-" + ("U" if dy ==  1 else "D" ) )

    def onDeath( self ) :
    
        Itchy.getGame().sceneDirector.playerDied( self )
        self.killZs()
        Big.onDeath(self)

    def onHit( self, hitter, dx, dy ) :
        
        if hitter.hasTag("deadly") :
        
            Explosion(self.actor) \
                .gravity(-0.1) \
                .projectiles(5) \
                .fade(0.9, 3.5).vx(3,5).vy(-0.4,0.4) \
                .pose("fragment") \
                .createActor()

            Explosion(self.actor) \
                .gravity(-0.1) \
                .projectiles(5) \
                .fade(0.9, 3.5).vx(-3,-5).vy(-0.4,0.4) \
                .pose("fragment") \
                .createActor()

            self.killZs()
            self.actor.deathEvent("hit")
            self.removeFromGrid()


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )

