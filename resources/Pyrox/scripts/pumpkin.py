from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from collectable import Collectable

properties = ArrayList()

class Pumpkin(Collectable) :

    def onBirth(self) :
        super(Pumpkin,self).onBirth()
        self.removeTag("soft")
        self.addTag("flower")
        self.pollinated = False
        self.fixed = False

    def onPlacedOnGrid(self) :
        # Bees will move over me, so I need to be an alternative.
        self.makeAlternateOccupant()

    def onHalfInvaded( self, invader ) :
        if invader.hasTag("bee") :
            self.pollinate()
        else :
            super(Pumpkin,self).onHalfInvaded(invader)

    def onInvaded( self, invader ) :
        pass
        
    def pollinate(self) :
        self.event("pollinate", "pollinated")
        self.removeTag("enemySoft")
        
    def onMessage(self, message) :
        if message == "pollinated" :
            self.pollinated = True

    def tick(self) :
        
        if self.pollinated and not self.fixed :
            if self.look(0,0).role is self :
                self.unmakeAlternateOccupant()
                self.fixed = True
                self.addTag("soft")
                self.addTag( "roundedNE" )
                self.addTag( "roundedSE" )
                self.addTag( "roundedSW" )
                self.addTag( "roundedNW" )
        
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


