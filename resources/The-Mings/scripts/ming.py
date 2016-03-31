from common import *

from director import PIXELATION_SIZE
from job import *

STEP_UP_DOWN = 12

properties = ArrayList()

properties.add( StringProperty( "initialJob" ) )
properties.add( BooleanProperty( "umbrella" ) )

class Ming(AbstractRole) :

    def __init__(self) :
        self.initialJob = ""        
        self.umbrella = False

        self.direction = 1 # Either 1 or -1. Remembered even when falling/digging etc.
        self.job = None
        
    def onBirth(self):
        print "ming.onBirth"
        self.addTag( "ming" )

        self.lookLeftRight = self.createLooker( "lookLeftRight" )
        self.lookDown = self.createLooker( "lookDown" )

        self.job = Walker()
        self.job.start(self)
        
        self.job.assignJob( self, self.initialJob )
        
        self.prevX = self.actor.x
        self.prevY = self.actor.y
        
    def onDeath(self) :
        self.job.quit(self)
        self.lookLeftRight.actor.kill()
        self.lookDown.actor.kill()
    
    def tick(self):

        self.job.tick( self )

        self.prevX = self.actor.x
        self.prevY = self.actor.y

    def onMessage( self, message ) :
        self.job.onMessage( self, message )


    def createLooker(self, name) :
        result = FollowerBuilder( self.actor ).companion( name ).create()
        result.event( "look" + self.directionLetter() )
        result.actor.appearance.alpha = 0
        return result


    def createRemover(self, name) :
        result = FollowerBuilder( self.actor ).companion( name ).create()
        result.event( "mask" )
        result.event( "mask" + self.directionLetter() )
        
        result.actor.appearance.alpha = 0
        return result


    def assignJob( self, jobName ) :
        return self.job.assignJob( self, jobName )

        
    def changeJob( self, job ) :
        self.actor.setAnimation( None, Actor.AnimationEvent.REPLACE )
        if self.job is not None :
            self.job.quit( self )
        self.job = job
        self.job.start( self )


    def directionLetter(self) :
        if self.direction < 0 :
            return "L"
        else :
            return "R"


    def look( self, looker, tags ) :
        looker.tick()
        looker.collisionStrategy.update()
        result = looker.collided( tags )
        return result
        

    def somethingBelow( self ) :
        return self.look( self.lookDown, "ground" );


    def somethingInfront( self ) :
        return self.look( self.lookLeftRight, "solid" );


    def checkForReversing( self ) :

        if self.somethingInfront() :
            self.reverse()
            return True

        for blocker in self.lookLeftRight.collisions("blocker") :
            # Am I heading towards the blocker?
            if (self.direction > 0) != (blocker.actor.x < self.actor.x) :
                self.reverse()
                return True

        return False

    def reverse(self) :
        self.actor.x = self.prevX
        self.actor.y = self.prevY
        self.direction = - self.direction
        self.lookLeftRight.event( "look" + self.directionLetter() )

        self.changeJob( Walker() )

    def checkForFalling( self ) :

        if not self.somethingBelow() :

            # Try moving down a little bit
            for i in range(0,STEP_UP_DOWN) :
                self.actor.moveBy(0,-1)
                if self.somethingBelow() :
                    return False
            
            # Nothing under us. Fall!
            if self.umbrella :
                self.changeJob( Floater() )
            else :
                self.changeJob( Faller() )
            return True

        return False


    def findLevel( self ) :
        # Move up till we aren't on solid ground
        for i in range(0,STEP_UP_DOWN) :
            self.actor.moveBy(0,1)
            if not self.somethingBelow() :
                self.actor.moveBy(0,-1)
                return
        
        # Too far up, go back to where we were.
        self.actor.moveBy(0,-STEP_UP_DOWN)


    def removeSolids( self, follower ) :
        follower.getCollisionStrategy().update()
        for solid in follower.collisions( "breakable" ) :
            self.removeSolid( follower, solid )

    def removeSolid( self, follower, solid ) :

        followerPose = follower.actor.appearance.pose
        solid.actor.appearance.fixAppearance()
        solidPose = solid.actor.appearance.pose

        tx = follower.actor.x - solid.actor.x
        tx += solidPose.offsetX - followerPose.offsetX

        ty = solid.actor.y - follower.actor.y
        ty -= followerPose.offsetY - solidPose.offsetY

        surface = solidPose.surface.copy()

        rect = Rect( 0,0, followerPose.surface.width, followerPose.surface.height )
        followerPose.surface.blit( rect, surface, int(tx), int(ty), Surface.BlendMode.RGBA_MULT )
        newPose = ImagePose( surface, solidPose.offsetX, solidPose.offsetY )
        newPose.direction = solidPose.direction
        solid.actor.appearance.pose = newPose


    def createCostumeFeatures(self,costume) :
        return MingFeatures(costume)
        
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )

class MingFeatures(PlainCostumeFeatures) :

    def __init__(self, costume) :        
        self.fragments = Fragments().pieces(20).create(costume)


