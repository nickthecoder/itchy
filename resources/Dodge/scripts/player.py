from common import * #@UnusedWildImport
from uk.co.nickthecoder.itchy.util import Filter

properties = ArrayList()
properties.add( DoubleProperty( "maxSpeed" ) )
properties.add( DoubleProperty( "clanPeriod" ).hint("seconds") )
properties.add( StringProperty( "clans" ) )

featureProperties = ArrayList()
featureProperties.add( StringProperty( "clan" ) )

class Player(AbstractRole) :

    def __init__(self) :
        self.maxSpeed = 10
        self.clanPeriod = 0
        self.clans = ""
        self.clanTimer = None

    def onBirth( self ) :

        self.clan = self.costumeFeatures.clan
        self.nextClan = self.clan
        self.clanFilter = ClanFilter(self)
        
        if self.clanPeriod > 0 :
            self.clanTimer = Timer.createTimerSeconds( self.clanPeriod )
            self.clanArray = self.clans.split(",")
            self.clanIndex = 0
            

    def onSceneCreated(self) :
        self.view = game.layout.findView( "main" )

    def tick(self):
    
        if self.clanTimer and self.clanTimer.isFinished() : 
            self.clanIndex += 1
            if self.clanIndex >= len( self.clanArray ) :
                self.clanIndex = 0
            self.nextClan = self.clanArray[self.clanIndex]
            self.event( self.nextClan )
            self.clanTimer.reset()

        
        # Move to the mouse, but don't let the player move too quickly
        mx = self.view.mouseX
        my = self.view.mouseY
        if self.actor.position.distance( mx, my ) < self.maxSpeed :
            self.actor.moveTo( mx, my )
        else :
            self.actor.setHeading( self.actor.position.directionDegrees( mx, my ) )
            self.actor.moveForwards( self.maxSpeed )

        if not game.sceneDirector.playing :
            return

        if self.collided( "wall" ) :
            self.die()
                        
        if self.collided( self.clanFilter, "enemy" ) :
            self.die()
            
            
    def die(self) :
        game.sceneDirector.stopPlaying()

        ExplosionBuilder(self.actor).fragments(self.costumeFeatures.fragments) \
            .fade(3).speed(1.5, 4, 0,0).gravity(-0.1) \
            .spin(-1, 1).create();

                
        self.deathEvent( "dead" )


    def onMessage( self, message ) :
            
        if message == "nextClan" :
            self.clan = self.nextClan            
            self.actor.costume = game.resources.getCostume( "player-" + self.nextClan )
            self.event( "default" )

                
    def createCostumeFeatures(self,costume) :
        return PlayerFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class ClanFilter( Filter ) :

    def __init__( self, player ) :
        self.player = player
        
    def accept( self, role ) :
        if role.hasTag( self.player.clan ) :
            return False
        if role.hasTag( self.player.nextClan ) :
            return False
        return True
        


class PlayerFeatures(CostumeFeatures) :

    def __init__(self, costume) :
        self.fragments = Fragments().pieces(20).create(costume)
        self.clan = "none"

    # Boiler plate code - no need to change this
    def getProperties(self):
        return featureProperties

