from common import *

from director import PIXELATION_SIZE

properties = ArrayList()

class Ming(AbstractRole) :

    def __init__(self) :
        self.dx = 0
        self.dy = 0
        self.direction = 1 # Either 1 or -1. Remembered even when falling/digging etc.
        self.job = None
        
    def onBirth(self):

        self.addTag( "ming" )

        self.lookLeftRight = self.createLooker( "lookLeftRight" )
        self.lookDown = self.createLooker( "lookDown" )
        self.lookStepUp = self.createLooker( "lookStepUp" )

        self.changeJob( Walker() )
        
        
    def createLooker(self, name) :
        result = FollowerBuilder( self.actor ).companion( name ).create()
        result.event( "look" + self.directionLetter() )
        result.actor.appearance.alpha = 0
        return result
        

    def tick(self):

        self.actor.moveBy( self.dx, self.dy )
        self.job.work( self )
        self.collisionStrategy.update()


    def onMessage( self, message ) :
        self.job.onMessage( self, message )


    def setJob( self, jobName ) :

        # Can't change a job while falling
        if isinstance( self.job, Faller ) :
            return

        newJob = None
        if jobName == "blocker" :
            newJob = Blocker()
            
        elif jobName == "smasher" :
            newJob = Smasher()

        elif jobName == "builder" :
            newJob = Builder()

        elif jobName == "digger" :
            newJob = Digger()

        if newJob is not None :
            self.changeJob( newJob )


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
            

    def reverse( self ) :
        print "Reverse!"
        self.direction = - self.direction
        self.lookLeftRight.event( "look" + self.directionLetter() )
        self.lookStepUp.event( "look" + self.directionLetter() )
        self.dx = - self.dx


    def look( self, looker, tags = ["solid"] ) :
        print "WR ", looker.actor.appearance.worldRectangle
        looker.tick()
        looker.collisionStrategy.update()
        result = looker.collided( tags )
        return result
        
        
    def checkForReversing( self ) :

        self.lookLeftRight.tick()
        self.lookLeftRight.collisionStrategy.update()
        for role in self.lookLeftRight.collisions( ["solid", "blocker"] ) :
        
            self.reverse()
            self.changeJob( Walker() )
            return True

        return False


    def checkForStepUp( self ) :

        if self.look( self.lookStepUp ) :
            self.findLevel()
            return True
        return False


    def checkForFalling( self ) :

        # Is there anything under my feet?
        if not self.look( self.lookDown ) :
            for i in range(0,8) :
                self.actor.moveBy(0,-1)
                if self.look( self.lookDown ) :
                    print "Small step down"  
                    return False
            
            # Nothing under us. Fall!
            print "Fall"
            self.changeJob( Faller() )
            return True

        return False



    def findLevel( self ) :
        # Move up till we aren't on solid ground
        for i in range(0,8) :
            self.actor.moveBy(0,1)

            if self.look( self.lookLeftRight ) :
                self.actor.moveBy(0,-1)
                print "Hitting LR"
                return

            if not self.look( self.lookDown ) :
                self.actor.moveBy(0,-1)
                return



    def removeSolids( self, follower ) :
        follower.getCollisionStrategy().update()
        for solid in follower.collisions( "solid" ) :
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
        followerPose.surface.blit( rect, surface, int(tx), int(ty), Surface.BlendMode.RGBA_SUB )
        newPose = ImagePose( surface, solidPose.offsetX, solidPose.offsetY )
        solid.actor.appearance.pose = newPose


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )

class Job() :

    def start(self, ming) :
        pass
        
    def work( self, ming ) :
        if ming.checkForFalling() :
            return
        
    def quit( self, ming ) :
        pass    
        
    def onMessage( self, ming, message ) :
        pass    


class Stop(Job) :

    def start(self, ming) :
        print "Stop"
        ming.dx = 0
        ming.dy = 0
        
    def work(self, ming) :
        pass

class Walker(Job) :

    def start(self, ming) :
        print "Walker", ming.directionLetter()
        ming.dy = 0
        ming.dx = 0
        ming.event( "walk" + ming.directionLetter() )
    
    def onMessage(self, ming, message) :
        if message == "step5" :
            ming.actor.moveBy( ming.direction * 5 * PIXELATION_SIZE, 0 )
        if message == "step4" :
            ming.actor.moveBy( ming.direction * 4 * PIXELATION_SIZE, 0 )

    def work( self, ming ) :
        if ming.checkForFalling() :
            return

        ming.checkForStepUp()

        if ming.checkForReversing() :
            return
        
class Faller(Job) :
    
    def start( self, ming ) :
        print "Faller"
        ming.dx = 0
        ming.dy = -1
        self.fallCount = 0
        ming.event( "fall" )

    def work( self, ming ) :

        self.fallCount -= ming.dy
        ming.dy -= 0.2 # Accelerate a little
        if ming.dy < -8 :
            ming.dy = -8 # Reached terminal velocity, don't get too fast.

        # Have I hit bottom?
        if ming.look( ming.lookDown ) :
            print "Stopped falling"

            # Have I fallen too far to land safely?
            if self.fallCount > 250 :

                ming.findLevel()
                ming.deathEvent( "squish" )
                ming.dy = 0
                ming.dx = 0

            else :
                ming.findLevel()
                ming.changeJob( Walker() )

class Builder(Job) :

    def start( self, ming ) :
        print "Builder"
        ming.dy = 0
        ming.dx = 0
        ming.event( "builder" + ming.directionLetter() )
    
    def work(self,ming) :
        pass
    
    def onMessage( self, ming, message ) :
        if message == "laidBrick" :
            brick = ming.actor.createCompanion( "brick" )
            brick.moveBy( ming.direction * 4 * PIXELATION_SIZE, 0 )
        if message == "steppedUp" :
            print "Step up"
            ming.actor.x += 2 * PIXELATION_SIZE * ming.direction
            ming.actor.y += PIXELATION_SIZE
            if ming.checkForReversing() :
                ming.changeJob( Walker() )
        if message == "jobComplete" :
            ming.changeJob( Walker() )

class Blocker(Job) :
    
    def start( self, ming ) :
        print "Blocker"
        ming.dx = 0
        ming.dy = 0
        ming.addTag( "blocker" )
        ming.event( "blocker" )

    def quit( self, ming ) :
        ming.removeTag( "blocker" )


class Smasher(Job) :

    def start( self, ming ) :
        print "Smasher"
        ming.dy = 0
        ming.dx = ming.direction # Slower than walking
        self.smashed = False # Set to true when first piece of solid is removed
        self.smashTimer = Timer.createTimerSeconds( 3 )

        self.smashFollower = Follower( ming )
        self.smashFollower.createActor()
        self.smashFollower.event( "smashed" )
        # Comment out this line (or change the value) to help debugging.
        self.smashFollower.actor.appearance.alpha = 0

        ming.event( "smasher" + ming.directionLetter() )
    
    def work( self, ming ) :
        if self.smashTimer.isFinished() :

            self.smashFollower.actor.kill()
            self.smashFollower = None
            ming.changeJob( Walker() )

        else :
            tester = ming.turnLeftTest if ming.direction > 0 else ming.turnRightTest
            if self.smashed :
                if not ming.checkCollision( tester ) :

                    ming.removeSolids( self.smashFollower )

                else :

                    self.smashFollower.actor.kill()
                    self.smashFollower = None
                    ming.changeJob( Walker() )
            else :
                if not ming.checkCollision( tester ) :

                    self.smashed = True


class Digger(Job) :

    def start( self, ming ) :
        print "Digger"
        ming.dy = 0
        ming.dx = 0
        self.removed = False # Set to true when first piece of solid is removed

        self.digFollower = ming.createLooker( "dig" )
        ming.event( "digger" )

    def work( self, ming ) :
        pass

    def onMessage( self, ming, message ) :
        print "Digger message", message
        
        if message == "jobComplete" :
            print "Complete"
            ming.changeJob( Walker() )
            
        if message == "dig" :
            print "Dig"
            ming.actor.moveBy( 0, -PIXELATION_SIZE )
            if not ming.look( self.digFollower ) :
                ming.changeJob( Walker() )
            else :
                ming.removeSolids( self.digFollower )

    def quit( self, ming ) :
        self.digFollower.actor.kill()
        

