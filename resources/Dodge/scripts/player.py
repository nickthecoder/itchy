from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "maxSpeed" ) )

class Player(AbstractRole) :

    def __init__(self) :
        self.maxSpeed = 10

    def tick(self):
        play = game.sceneDirector
        
        # Move to the mouse, but don't let the player move too quickly
        mx = Itchy.getMouseX()
        my = 600-Itchy.getMouseY()
        if self.actor.distanceTo( mx, my ) < self.maxSpeed :
            self.actor.moveTo( mx, my )
        else :
            self.actor.setHeading( self.actor.directionOf( mx, my ) )
            self.actor.moveForwards( self.maxSpeed )

        if not play.playing :
            return
                        
        if self.collided( "deadly" ) :
            play.stopPlaying()

            ExplosionBuilder(self.actor) \
                .fragments( self.costumeFeatures.fragments ) \
                .speed(0.5,0.5,0.5,0.5).fade(2).vy(-3,3).vx(-3,3).gravity(-0.1) \
                .create()
                
            self.deathEvent( "dead" )
                
    def createCostumeFeatures(self,costume) :
        return PlayerFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class PlayerFeatures(CostumeFeatures) :

    def __init__(self, costume) :
        super(PlayerFeatures,self).__init__(costume)
        self.fragments = Fragments().pieces(20).create(costume)


