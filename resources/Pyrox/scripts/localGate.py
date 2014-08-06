from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy import Actor
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty

from gridRole import GridRole

properties = ArrayList()
properties.add( StringProperty( "destinationId" ) )

# A "jump gate", which transports an item to another gate in the SAME scene.
# This is unlike the classes Warp and Gate, which both transfer to NEW scenes.
# The destination is given by a Role ID (ids are set in the first field of the "Role" tab in the scene designer).
# If a destination is not found, then no jump is performed.
class LocalGate(GridRole) :

    def __init__(self) :
        GridRole.__init__(self)
        self.destinationId = ""
        self.working = False
        
    def onSceneCreated(self):
        GridRole.onSceneCreated(self)
        
        self.makeAlternateOccupant()
        self.addTag("squashN")
        self.addTag("squashS")
        self.addTag("squashE")
        self.addTag("squashW")

    def onHalfInvaded(self,invader) :
    
        if self.destinationId == "" :
            return

        destination = Itchy.getGame().findRoleById( self.destinationId )
        if destination is None :
            return
            
        self.working = True
        
        # Temporarily use MY costume, because that has the enter and exit animations.
        costume = invader.actor.costume
        invader.actor.costume = self.actor.costume
        invader.event("enter") # Shrinks and fades whatever enters
        invader.actor.costume = costume
        
        self.event("jump") # Makes a sound effect
        invader.currentSpeed = 1

    def onInvaded(self, invader) :

        if self.working :
            self.working = False
            # Temporarily use MY costume, because that has the enter and exit animations.
            costume = invader.actor.costume
            invader.actor.costume = self.actor.costume
            invader.event("exit")
            invader.actor.costume = costume

            destination = Itchy.getGame().findRoleById( self.destinationId )
            if destination :
                invader.moveTo(destination.actor.x, destination.actor.y)
                self.event("jumped") # Makes another sound effect  


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


