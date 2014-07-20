from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName



from java.util import ArrayList

from gridRole import GridRole
import gridRole

properties = ArrayList()

class Player(GridRole) :

    def __init__(self) :   
        super(Player, self).__init__()
        self.roleName = "Player"
        self.inputLeft = Input.find("left")
        self.inputRight = Input.find("right")
        self.inputUp = Input.find("up")
        self.inputDown = Input.find("down")

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
            
            if self.inputLeft.pressed() :
                self.attemptToMove( -1, 0 )
            
            elif self.inputRight.pressed() :
                self.attemptToMove( 1, 0 )
            
            elif self.inputUp.pressed() :
                self.attemptToMove( 0, 1 )
                
            elif self.inputDown.pressed() :
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
        if hitter.hasTag("heavy") :
            self.actor.kill()


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


