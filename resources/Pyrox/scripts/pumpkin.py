from common import *

from collectable import Collectable

properties = ArrayList()

class Pumpkin(Collectable) :

    def onBirth(self) :
        super(Pumpkin,self).onBirth()
        self.removeTag("soft") # Can't be collected till pollinated.
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
        print "Polinating pumpkin"
        self.event("pollinate", "pollinated")
        self.removeTag("flower")
        
    def onMessage(self, message) :
        if message == "pollinated" :
            print "Pollinated"
            self.pollinated = True

    def tick(self) :
        
        # When we have been pollinated, convert to a regular collectable as soon as the
        # bee has moved out of the way.
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


