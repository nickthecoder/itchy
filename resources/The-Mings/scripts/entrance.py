from common import *

from ming import Ming

properties = ArrayList()
properties.add( IntegerProperty( "mings" ) )
properties.add( DoubleProperty( "period" ) )
properties.add( IntegerProperty( "offsetX" ) )
properties.add( IntegerProperty( "offsetY" ) )

class Entrance(AbstractRole) :

    def __init__(self) :
        self.mings = 1
        self.period = 2
        self.offsetX = 0        
        self.offsetY = -200


    def onBirth(self):
        Itchy.getGame().getSceneDirector().scrollTo( self.getActor().getX() + self.offsetX, self.getActor().getY() +self.offsetY )
        self.timer = Timer.createTimerSeconds( self.period )


    def tick(self):
        if self.mings > 0 :
            if self.timer.isFinished() :
                self.mings -= 1
                self.timer.reset()
                self.createMing()


    def createMing( self ) :
        print "Creating a ming"
        costume = Itchy.getGame().resources.getCostume("ming")
        actor = Actor(costume)
        actor.setRole( Ming() )
        actor.moveTo(self.getActor())
        actor.setZOrder( self.getActor().getZOrder() -1 )
        self.getActor().getStage().add(actor)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


