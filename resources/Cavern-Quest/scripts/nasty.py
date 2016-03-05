from common import * #@UnusedWildImport

from movable import Movable

properties = ArrayList()

class Nasty(Movable) :

    def __init__(self) :
        super(Nasty,self).__init__()
        self.direction = 0 # 0..3 North,East,South,West
        self.allowReverse = True

    def onBirth(self) :
        super(Nasty,self).onBirth()
        self.addTag( "hittable" )
        self.addTag( "squashable" )
        
    def tick(self):
        if self.idle :
            self.chooseMovement()        
        else :
            self.tickMove()
            
    def chooseMovement( self ) :
    
        player = Itchy.getGame().sceneDirector.player
        #print self.actor.x, self.actor.y, " verses ", player.actor.x, player.actor.y

        # If we are heading directly towards or away from the player, then do not try left or right
        if player.actor.y == self.actor.y and (self.direction % 2) == 0 :
            # When heading into an East/West cul-de-sac, then get stuck. This makes it easy to trap
            # the nasties, but can also make life tricky for the player in some circumstances.
            self.allowReverse = False
            newDir = 0 if (player.actor.x > self.actor.x) else 2
            #print "Direct E/W", newDir

        elif player.actor.x == self.actor.x and (self.direction % 2) == 1 :
            newDir = 1 if (player.actor.y > self.actor.y) else 3
            #print "Direct N/S", newDir

        else :

            # Try not to move forwards again, so look LEFT first
            newDir = (self.direction + 5) % 4
            #print "looking left first", newDir
            
            # If we are heading away, then try RIGHT first (ignore left for now)
            if self.headingAway( newDir, player ) :
                newDir = (newDir + 6) % 4
                #print "No. changed my mind. looking right first", newDir

            if not self.canMove( newDir ) :
                # Couldn't turn one way, so lets try turning the other way.
                # This will be away from the player, but not in a straight line.
                newDir = (newDir + 6) % 4
                #print "looking the other way", newDir
                if not self.canMove( newDir ) :
                    # We couldn't turn left, or right, so lets try straight ahead (keep direction unchanged).
                    if self.canMove( self.direction ) :
                        newDir = self.direction
                        #print "sticking with ", newDir
                    else :
                        if self.allowReverse :
                            #print "Ok, can't turn left, right or forwards, let's reverse"
                            newDir = (self.direction + 6) % 4
                            if not self.canMove( newDir ) :
                                #print "Well and truely stuck!"
                                pass

            if self.canMove( newDir ) :
                self.allowReverse = True

        if self.canMove( newDir ) :            
            self.direction = newDir
            #print "moving", self.direction
            self.moveDirection( self.direction )


    def canMove( self, direction ) :
        ahead = self.lookDirection( direction )
        if ahead.hasTag( "soft" ) :
            return True
            
            
    def headingAway( self, direction, player ) :
    
        dx = self.getDeltaX( direction )
        dy = self.getDeltaY( direction )
        
        tx = player.actor.x - self.actor.x
        ty = player.actor.y - self.actor.y
        
        if dx * tx > 0 :
            return False
            
        if dy * ty > 0 :
            return False
            
        return True
    
    
    def hit( self, x, y ) :
        self.removeTag( "hittable" )
        self.removeTag( "squashable" )
        self.actor.moveTo( x, y )
        self.deathEvent( "squash" )
    

    def onDeath( self ) :
        grid = self.square.grid
        Itchy.getGame().sceneDirector.respawn( self )
        super(Nasty,self).onDeath()
        
        soil = self.actor.createCompanion("soil").role
        soil.placeOnGrid( grid )

    def onMessage( self, message ) :
        if self.actor.isVisible() :
            # Play the sound in time with the ey movements.
            if message == "play" :
                self.event("play")
    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


