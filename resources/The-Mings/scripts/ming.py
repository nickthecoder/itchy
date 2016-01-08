from common import *

from director import PIXELATION_SIZE

properties = ArrayList()

class Ming(AbstractRole) :

    def __init__(self) :
        # TODO Initialise your object. Note you can't access self.getActor() yet. e.g. :
        self.dx = 2
        self.dy = 0
        self.direction = 1 # Either 1 or -1. Remembered even when falling/digging etc.
        self.job = None
        
    def onBirth(self):

        self.fallTest = self.createCollisionTest( "fall" )
        self.turnRightTest = self.createCollisionTest( "turnRight" )
        self.turnLeftTest = self.createCollisionTest( "turnLeft" )
        self.stepUpTest = self.createCollisionTest( "stepUp" )
        self.addTag( "ming" )

        self.direction = 1 # Either 1 or -1. Remembered even when falling/digging etc.
        self.changeJob( Walker() )
        

    def createCollisionTest( self, name ) :
        actor = FollowerBuilder( self.actor ).pose( name ).create().actor
        actor.setZOrder( 100 )
        # Make it invisible, comment out this line while debugging motions.
        #actor.getAppearance().setAlpha( 0 )
        return actor.role


    def checkCollision( self, role, checkForBlockers=False ) :
        role.getCollisionStrategy().update()
        if not role.getCollisionStrategy().collisions( role.getActor(), "solid" ).isEmpty() :
            return False

        if checkForBlockers :
            if not role.getCollisionStrategy().collisions( role.getActor(), "blocker" ).isEmpty() :
                return False

        return True


    def tick(self):

        self.getActor().moveBy( self.dx, self.dy )
        self.job.work( self )
        self.getCollisionStrategy().update()


    def onMessage( self, message ) :
        print "Ming message", message
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

        if newJob is not None :
            self.changeJob( newJob )


    def changeJob( self, job ) :
        if self.job is not None :
            self.job.quit( self )
        self.job = job
        self.job.start( self )


    def directionLetter(self) :
        if self.direction < 0 :
            return "L"
        else :
            return "R"


    def checkForFalling( self ) :

        # Is there anything under my feet?
        if self.checkCollision( self.fallTest ) :

            # If it is a small step down, don't fall, just change Y
            self.getActor().moveBy(0,-8)
            self.fallTest.tick()
            if not self.checkCollision( self.fallTest ) :
                #print "Small step down"
                self.findLevel()
                return False
            self.getActor().moveBy(0,8)
            self.fallTest.tick()

            # Nothing under us. Fall!
            #print "Fall"
            self.changeJob( Faller() )
            return True

        return False


    def checkForStepUp( self ) :

        if not self.checkCollision( self.stepUpTest ) :
            self.findLevel()
            return True
        return False


    def checkForReversing( self ) :

        if self.direction > 0 :
            # Is there something solid to my right?
            if not self.checkCollision( self.turnLeftTest, True ) :
                self.direction = -1
                self.changeJob( Walker() )
                return True

        else :
            # Is there something solid to my left?
            if not self.checkCollision( self.turnRightTest, True ) :
                self.direction = 1
                self.changeJob( Walker() )
                return True

        return False


    def findLevel( self ) :
        # Move up till we aren't on solid ground
        while not self.checkCollision( self.fallTest ) :
            self.getActor().moveBy(0,1)
            self.fallTest.tick() # Allow the follow to follow me upwards
            self.fallTest.getCollisionStrategy().update()
        # Move down 1 pixel, and we are now perfectly level with the ground.
        self.getActor().moveBy(0,-1)


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
        pass
        
    def quit( self, ming ) :
        pass    
        
    def onMessage( self, ming, message ) :
        pass    


class Walker(Job) :

    def start(self, ming) :
    
        ming.dy = 0
        ming.dx = ming.direction * 2
        ming.event( "walk" + ming.directionLetter() )
    
    def work( self, ming ) :
        if ming.checkForFalling() :
            return
        if ming.checkForReversing() :
            return
        ming.checkForStepUp()
        
class Faller(Job) :
    
    def start( self, ming ) :
    
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
        if not ming.checkCollision( ming.fallTest ) :

            # Have I fallen too far to land safely?
            if self.fallCount > 250 :

                ming.findLevel()
                ming.deathEvent( "squish" )
                ming.dy = 0
                ming.dx = 0

            else :
                ming.findLevel()

                if ming.direction > 0 :
                    ming.event( "walkR" )
                else :
                    ming.event( "walkL" )
                ming.dy = 0
                ming.dx = ming.direction * 2
                ming.changeJob( Walker() )

class Builder(Job) :

    def start( self, ming ) :
        ming.dy = 0
        ming.dx = 0
        ming.event( "builder" + ming.directionLetter() )
    
    def onMessage( self, ming, message ) :
        if message == "layedBrick" :
            brick = ming.actor.createCompanion( "brick" )
        if message == "steppedUp" :
            ming.actor.x += 2 * PIXELATION_SIZE * ming.direction
            ming.actor.y += PIXELATION_SIZE
            if ming.checkForReversing() :
                ming.changeJob( Walker() )
        if message == "jobComplete" :
            ming.changeJob( Walker() )

class Blocker(Job) :
    
    def start( self, ming ) :
        ming.dx = 0
        ming.dy = 0
        ming.addTag( "blocker" )
        ming.event( "blocker" )

    def quit( self, ming ) :
        ming.removeTag( "blocker" )


class Smasher(Job) :

    def start( self, ming ) :
        ming.dy = 0
        ming.dx = ming.direction # Slower than walking
        self.smashed = False # Set to true when first piece of solid is removed
        self.smashTimer = Timer.createTimerSeconds( 3 )

        self.smashFollower = Follower( ming )
        self.smashFollower.createActor()
        self.smashFollower.event( "smashed" )
        self.smashFollower.getActor().getAppearance().setAlpha( 0 )

        ming.event( "smasher" + ming.directionLetter() )
    
    def work( self, ming ) :
        if self.smashTimer.isFinished() :

            self.smashFollower.getActor().kill()
            self.smashFollower = None
            ming.changeJob( Walker() )

        else :
            tester = ming.turnLeftTest if ming.direction > 0 else ming.turnRightTest
            if self.smashed :
                if not ming.checkCollision( tester ) :

                    self.smashFollower.getCollisionStrategy().update()
                    for solid in self.smashFollower.getCollisionStrategy().collisions( self.smashFollower.getActor(), "solid" ) :
                        self.smashSolid( solid )
                else :

                    self.smashFollower.getActor().kill()
                    self.smashFollower = None
                    ming.changeJob( Walker() )
            else :
                if not ming.checkCollision( tester ) :

                    self.smashed = True


    def smashSolid( self, solid ) :

        smashedPose = self.smashFollower.getActor().getAppearance().getPose()
        solidPose = solid.getActor().getAppearance().getPose()

        tx = self.smashFollower.getActor().getX() - solid.getActor().getX()
        tx += solidPose.getOffsetX() - smashedPose.getOffsetX()

        ty = solid.getActor().getY() - self.smashFollower.getActor().getY()
        ty -= smashedPose.getOffsetY() - solidPose.getOffsetY()

        surface = solidPose.getSurface().copy()

        rect = Rect( 0,0, smashedPose.getSurface().getWidth(), smashedPose.getSurface().getHeight() )
        smashedPose.getSurface().blit( rect, surface, int(tx), int(ty), Surface.BlendMode.RGBA_SUB )
        newPose = ImagePose( surface, solidPose.getOffsetX(), solidPose.getOffsetY() )
        solid.getActor().getAppearance().setPose( newPose )

