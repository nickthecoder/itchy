from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import Talk
from uk.co.nickthecoder.itchy.role import Explosion

from java.util import ArrayList

from gridRole import GridRole
import gridRole
from movable import Movable

properties = ArrayList()

class Player(Movable) :

    def __init__(self) :
    
        super(Player, self).__init__()

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
        
        self.talkActor = None

    def onBirth( self ) :
        super(Player, self).onBirth()

    def onAttach( self ) :    
        super(Player, self).onAttach()
        self.speed = 6
        Itchy.getGame().getDirector().gridView.centerOn(self.actor)
        self.addTag("hittable")
        self.addTag("player")

    # Called by Level to let me find the "warp" of the scene that's just been completed.
    def getReady( self ) :
        for warp in Itchy.getGame().findRoleByTag("warp") :
            director = Itchy.getGame().getDirector()
            if director.previousSceneName == warp.scene :
                x = warp.getActor().getX() + warp.exitX * director.squareSize
                y = warp.getActor().getY() + warp.exitY * director.squareSize
                self.moveTo( x, y )
                break
                
        for portcullis in Itchy.getGame().findRoleByTag("portcullis") :
            portcullis.getReady(self)
            
    def talk( self, message ) :
        if self.talkActor :
            self.talkActor.kill()
            
        message = message.replace("\\n","\n")

        self.talkActor = Talk(self) \
            .bubble("talk").font("Vera", 14).margin(15, 10, 45, 10).offset(0,60).text(message) \
            .createActor()

        self.talkActor.getStage().addTop(self.talkActor)
        self.talkActor.setCostume( self.getActor().getCostume() )
        self.talkActor.event("talk-fade")
                        
            
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
            
        if (self.isMoving()) :
            pass
            
        else :
            
            if self.inputLeft.pressed() :
                self.attemptToMove( -1, 0 )
            
            elif self.inputRight.pressed() :
                self.attemptToMove( 1, 0 )
            
            elif self.inputUp.pressed() :
                self.attemptToMove( 0, 1 )
                
            elif self.inputDown.pressed() :
                self.attemptToMove( 0, -1 )

        tx = self.actor.getX() + self.scrollOffsetX
        ty = self.actor.getY() + self.scrollOffsetY
        
        Itchy.getGame().getDirector().centerOn( tx, ty )
    
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
        obj = self.look( dx, dy )
        if (obj.hasTag( "soft" ) or obj.hasTag( "squash" + gridRole.getDirectionAbreviation(dx,dy) )) :
            self.move(dx, dy)
            return

        if obj.canShove(self,dx,dy,self.speed, 4) :
            obj.shove(self, dx, dy, self.speed)
            self.move(dx, dy)
    
    
    def onInvading( self ) :
        pass

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

            self.actor.deathEvent("hit")
            self.removeFromGrid()


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


