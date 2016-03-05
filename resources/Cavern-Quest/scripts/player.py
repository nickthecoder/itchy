from common import * #@UnusedWildImport

from movable import Movable

properties = ArrayList()

game = Itchy.getGame()

#
# The main character in the game, i.e. the one that the user is in controll of.
# The views are centered on the Player, but this can be offset somewhat using Home/End/PgUp/PgDn.
#
class Player(Movable) :

    def __init__(self) :
        super(Player,self).__init__()
        
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
        
        self.digging = None

               
    def onBirth(self) :
        super(Player, self).onBirth()
        self.addTag("player")
        self.addTag("soft")
        self.addTag("squashable")
        self.addTag("hittable")
        
   
    # The player's tick is special - it is called before all other objects on the grid.
    # This is so that it is easier to predict what will happen to objects near us.
    # For example, we don't want objects to the left to act different to those to the right
    # just because our tick happens before one and after the other.
    def tick(self) :
        # Do nothing - our tick code is in method playerTick. See : level.tick()
        pass

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

        if self.square is None :
            return

        if self.idle :
            # Check inputs.
            self.movements()
        else :
            # Continue moving in the direction of travel
            self.tickMove()

        tx = self.actor.x + self.scrollOffsetX
        ty = self.actor.y + self.scrollOffsetY
        
        game.director.centerOn( tx, ty )


    def movements(self) :

        if self.digging is not None :
            if not self.digging.actor.isDead() :
                return
            self.digging = None

        if self.inputLeft.pressed() and self.attemptToMove( -1, 0 ) :
            return
        
        elif self.inputRight.pressed() and self.attemptToMove( 1, 0 ) :
            return
            
        elif self.inputUp.pressed() and self.attemptToMove( 0, 1 ) :
            return
            
        elif self.inputDown.pressed() and self.attemptToMove( 0, -1 ) :
            return
            
    
    def attemptToMove( self, dx, dy ) :

        ahead = self.look( dx, dy )
        if ahead.hasTag("soil") :
            if game.sceneDirector.blasts > 0 :
                self.event( "blast" )
                ahead.dig()
                game.sceneDirector.blasts -= 1
                self.digging = ahead
                return True
            else :
                self.event( "noBlasts" )
                return False
        
        if ahead.hasTag( "soft" ) :
            self.move(dx, dy)
            return True
        
        return False

    def onHalfInvaded(self, invader) :
        self.killMe()

    def hit( self, x, y ) :
        self.killMe()


    def killMe( self ) :
        ExplosionBuilder(self.actor) \
            .gravity(-0.1) \
            .projectiles(5) \
            .fade(0.9, 3.5).vx(3,5).vy(-0.4,0.4) \
            .pose("fragment") \
            .create()

        ExplosionBuilder(self.actor) \
            .gravity(-0.1) \
            .projectiles(5) \
            .fade(0.9, 3.5).vx(-3,-5).vy(-0.4,0.4) \
            .pose("fragment") \
            .create()


        self.removeFromGrid()
        self.deathEvent("die")


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
    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )

