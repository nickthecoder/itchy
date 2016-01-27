from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import DoubleProperty

properties = ArrayList()
properties.add( DoubleProperty( "speed" ) )

class Player(AbstractRole) :

    def __init__(self) :
        self.speed = 1
        self.collected = 0
                
    def onBirth(self):

        self.inputLeft = Input.find("left")
        self.inputRight = Input.find("right")
        self.inputUp = Input.find("up")
        self.inputDown = Input.find("down")

    def tick(self):
        oldX = self.actor.x
        oldY = self.actor.y
        
        if self.inputLeft.pressed() :
            self.actor.moveBy( -self.speed, 0 )

        if self.inputRight.pressed() :
            self.actor.moveBy( self.speed, 0 )

        if not self.getCollisionStrategy().collisions(self.getActor(),"solid").isEmpty() :
            self.actor.moveTo( oldX, oldY )
            
        oldX = self.actor.x
        oldY = self.actor.y

        if self.inputUp.pressed() :
            self.actor.moveBy( 0, self.speed )

        if self.inputDown.pressed() :
            self.actor.moveBy( 0, -self.speed )

        if not self.getCollisionStrategy().collisions(self.getActor(),"solid").isEmpty() :
            self.actor.moveTo( oldX, oldY )
            

        if not self.getCollisionStrategy().collisions(self.getActor(),"light").isEmpty() :
            Itchy.getGame().getDirector().restartScene()
            return
            
        i = self.getCollisionStrategy().collisions(self.getActor(),"exit").iterator()
        if i.hasNext() :
            exit = i.next()
            if self.collected >= Itchy.getGame().sceneDirector.collectables :
                Itchy.getGame().director.startScene( exit.nextLevel )

        i = self.getCollisionStrategy().collisions(self.getActor(),"collectable").iterator()
        while i.hasNext() :
            collectable = i.next()
            self.collected += collectable.collect()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


