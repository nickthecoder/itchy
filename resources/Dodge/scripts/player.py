from common import * #@UnusedWildImport

properties = ArrayList()

class Player(AbstractRole) :

    def __init__(self) :
        pass
        
    def onBirth(self):
        print "Fragments : ", self.costumeFeatures.fragments
        
    def tick(self):
        play = Itchy.getGame().sceneDirector
        
        if not play.playing :
            return
            
        self.actor.moveTo( Itchy.getMouseX(), 600-Itchy.getMouseY() )
            
        if self.collided( "deadly" ) :
            play.stopPlaying()

            ExplosionBuilder(self.actor) \
                .fragments( self.costumeFeatures.fragments ) \
                .speed(0.5,0.5,0.5,0.5).fade(2).vy(-3,3).vx(-3,3).gravity(-0.1) \
                .create()
                
            self.deathEvent( "dead" )
                
    def createCostumeFeatures(self,costume) :
        print "createCostumeFeatures"
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
        print "Creating player fragments"
        self.fragments = Fragments().pieces(20).create(costume)


