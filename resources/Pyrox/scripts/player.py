from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from uk.co.nickthecoder.jame.event import Keys

from java.util import ArrayList

from gridRole import GridRole
import gridRole

properties = ArrayList()

class Player(GridRole) :

    def __init__(self) :   
        super(Player, self).__init__()
        self.roleName = "Player"
    
    def onAttach( self ) :
        super(Player, self).onAttach()
        self.speed = 6
        Itchy.getGame().getDirector().gridView.centerOn(self.actor)
        self.addTag("hittable")
        self.addTag("player")


    def tick( self ) :
    
        Itchy.getGame().getDirector().gridView.centerOn(self.actor)

        if (self.isMoving()) :
            pass
        else :
            
            if (Itchy.isKeyDown(Keys.LEFT)) :
                self.attemptToMove( -1, 0 )
            elif (Itchy.isKeyDown(Keys.RIGHT)) :
                self.attemptToMove( 1, 0 )
            elif (Itchy.isKeyDown(Keys.UP)) :
                self.attemptToMove( 0, 1 )
            elif (Itchy.isKeyDown(Keys.DOWN)) :
                self.attemptToMove( 0, -1 )

        super(Player, self).tick()

    
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

    def onHit( self, hitter ) :
        self.actor.kill()


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


