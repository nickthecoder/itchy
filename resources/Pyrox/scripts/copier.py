from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.property import IntegerProperty
from uk.co.nickthecoder.itchy.role import Projectile

from java.util import ArrayList

from gridRole import GridRole

properties = ArrayList()
properties.add( IntegerProperty( "copies" ) )
properties.add( IntegerProperty( "dx" ) )
properties.add( IntegerProperty( "dy" ) )

class Copier(GridRole) :

    def __init__(self) :
        super(Copier,self).__init__()
        self.copies = 10
        self.dx = 1
        self.dy = 0
        self.copying = None
        self.waitTillEmpty = False
        self.speed = 4 # Speed to eject the copy

    def onPlacedOnGrid(self) :
        self.makeAlternateOccupant(True)
    
    def tick(self):
        north = self.lookNorth()

        if self.copying :
            return

        if self.waitTillEmpty :
            if north.isEmpty() :
                self.waitTillEmpty = False
            return

        if (not north.isEmpty()) and (not north.hasTag("player")) :
            self.copies -= 1
            if self.copies < 0 :
                self.waitTillEmpty = True
                self.talk("_empty")
            else :
                self.copying = north.actor.costume
                self.event("scan", "copied")
                Projectile(self).pose("light").fade(0).life(1).vx(.6).createActor()
    
    def onMessage(self, message) :
        if message == "copied" :
            if self.look(self.dx, self.dy).isEmpty() :
                squareSize = self.square.grid.squareSize
                self.event("copied")
                actor = Itchy.getGame().resources.createActor( self.copying, self.actor.stage )
                actor.moveTo( self.actor.x, self.actor.y )
                actor.role.placeOnGrid( self.square.grid )
                actor.role.move(self.dx, self.dy, self.speed)
            else :
                self.talk("_jam")

            self.copying = False
            self.waitTillEmpty = True


    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


