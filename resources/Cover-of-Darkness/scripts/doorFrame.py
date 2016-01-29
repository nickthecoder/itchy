from common import *

properties = ArrayList()

properties.add( BooleanProperty( "open" ) )
properties.add( BooleanProperty( "locked" ) )
properties.add( BooleanProperty( "pickPull" ) )
properties.add( BooleanProperty( "pickPush" ) )

class DoorFrame(AbstractRole) :

    def __init__(self) :
        self.open = False
        self.locked = False
        self.pickPush = True
        self.pickPull = True

    def onBirth(self):

        self.door = self.actor.createCompanion("door").role
        self.door.doorFrame = self
        self.door.locked = self.locked
        self.door.actor.direction = self.actor.direction
        self.door.actor.moveForwards( 6,32 )

        if self.open :
            self.door.actor.direction += self.door.angle;
            self.door.open = True
            self.door.addTag("clickable")

        self.pushMat = self.actor.createCompanion("mat").role
        self.pushMat.actor.direction = self.actor.direction + 180
        self.pushMat.door = self.door
        if self.pickPush :
            self.pushMat.pickable = True

        self.pullMat = self.actor.createCompanion("mat").role
        self.pullMat.actor.direction = self.actor.direction
        self.pullMat.door = self.door
        if self.pickPull :
            self.pullMat.pickable = True

        self.door.locked = self.locked
        
    def tick(self):
        pass



    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


