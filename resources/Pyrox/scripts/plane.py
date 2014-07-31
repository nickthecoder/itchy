from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from movable import Movable
import gridRole

properties = ArrayList()

class Plane(Movable) :

    def __init__(self) :
        Movable.__init__(self)
        self.direction = -1
        
    def onBirth(self):
        Movable.onBirth(self)
        self.actor.costume.properties.update(self)
        self.squash = "squashE" if self.direction == 1 else "squashW"

        self.addTag( "explosionTrigger" )
        self.addTag( "deadly" );
        
        # Keep note if the plane was just pushed, or if its had a time to move on its own / been idle.
        # Used to determin if the plane can be pushed if there is clear air in front of it.
        self.pushed = False


    def onSceneCreated(self) :
        # Create a dummy role BEHIND me, because a plane takes up TWO squares in the grid.
        self.dummy = Dummy(self, -self.direction, 0)
            
    def tick(self) :

        if not self.isMoving() :
                
            self.pushed = False
            forward = self.look( self.direction, 0 )

            if forward.hasTag(self.squash) :
                self.move(self.direction, 0)

        Movable.tick(self)

        # Check that dummy hasn't been removed from the grid (which will happen if my tick caused it to die)
        if self.dummy.square :
            self.dummy.tick()
        
        
    def canShove( self, pusher, dx, dy, speed, force) :


        if self.isMoving() or self.dummy.isMoving() :
            return False

        if (force < 4) :
            return False

        # If we've been pushed, then check to see if we can fly forwards, if so, we can't be pushed.
        # If we haven't been pushed, then we ignore the fact that we fly forwards, and allow ourselves to be pushed.
        # This allows things to push pushed ONCE (i.e. across just ONE square) before the plane moves forwards.
        if self.pushed :
        
            # Will be move by ourselves (falling or rolling), if so, don't let us be pushed.
            #if self.pushed :
            ahead = self.look( self.direction, 0 )
            if ahead.hasTag(self.squash) :
                return False

        if dy == 0 and dx == -self.direction :
            forward = self.look(dx * 2, dy )
        else :
            forward = self.look(dx, dy) # In the direction of the push...


        if forward.isMoving() :
            return False

        squashD = "squash" + gridRole.getDirectionAbreviation(dx, dy)
        if forward.hasTag( squashD ) :
            dummyForward = self.dummy.look(dx,dy)
            if dummyForward.isMoving() :
                return False
            if dummyForward.hasTag( squashD ) :
                return True

        return False
        

    def shove( self, pusher, dx, dy, speed ) :
        self.pushed = True
        Movable.shove(self, pusher, dx, dy, speed)
    
    
    def onArrived( self, dx, dy ) :
        # Don't hit things when I've been pushed, only when I've driven forwards.
        if dx == self.direction :
            forward = self.look(dx,dy)
            if (forward.hasTag("hittable")) :
                forward.onHit( self, dx, dy )

    def onDeath(self) :
        self.dummy.removeFromGrid()

        square = self.square
        Movable.onDeath(self)
        
    def move(self, dx, dy,speed=None) :
        Movable.move(self,dx,dy,speed)
        # Check if dummy is still on the grid (it won't be if my movement caused us both to die).
        if self.dummy.square :
            self.dummy.move(dx, dy, speed)
        

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class Dummy(Movable) :

    def __init__(self, parent, dx, dy) :

        super(Dummy,self).__init__()
        
        self.parent = parent
        self.speed = parent.speed

        x = parent.square.x + dx
        y = parent.square.y + dy
        self.square = parent.square.grid.getSquare(x, y)
        self.square.occupant = self
        self.addTag("explodable")
        self.addTag("roundedNW")
        self.addTag("roundedNE")


    def canShove( self, pusher, dx, dy, speed, force) :
        return self.parent.canShove(pusher, dx, dy, speed, force)
        
    def shove( self, pusher, dx, dy, speed=None ) :
        self.parent.shove(pusher, dx, dy, speed)
        
    def explode( self ) :
        print "Exploding dummy"
        self.parent.explode()

