from common import *

properties = ArrayList()

class Ming(AbstractRole) :

    def __init__(self) :
        self.dx = 2
        self.dy = 0
        self.direction = 1 # Either 1 or -1. Remembered even when falling/digging etc.
        self.action = self.walk
        
    def onBirth(self):
        self.fallTest = self.createCollisionTest( "fall" )        
        self.turnRightTest = self.createCollisionTest( "turnRight" )
        self.turnLeftTest = self.createCollisionTest( "turnLeft" )
        self.stepUpTest = self.createCollisionTest( "stepUp" )
        self.addTag( "ming" )

        self.direction = 1 # Either 1 or -1. Remembered even when falling/digging etc.
        self.walk_init()


    def createCollisionTest( self, name ) :
        
        actor = FollowerBuilder( self.getActor() ).pose( name ).create().getActor()
        actor.setZOrder( 100 )
        # Make it invisible, comment out this line while debugging motions.
        actor.getAppearance().setAlpha( 0 )
        return actor.getRole()


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
        self.action()
        self.getCollisionStrategy().update()


    def setJob( self, job ) :

        if self.action == self.fall :
            return

        if job == "blocker" :
            self.block_init()
        else :
            self.removeTag( "blocker" )

        if job == "smasher" :
            self.smash_init()


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
                self.findLevel()
                return False
            self.getActor().moveBy(0,8)
            self.fallTest.tick()

            # Nothing under us. Fall!
            self.fall_init()
            return True

        return False


    def checkForStepUp( self ) :

        if not self.checkCollision( self.stepUpTest ) :
            self.findLevel()
            return True
        return False


    def checkForReversing( self ) :

        if self.dx > 0 :
            # Is there something solid to my right?
            if not self.checkCollision( self.turnLeftTest, True ) :
                self.direction = -1
                self.walk_init()
                return True

        if self.dx < 0 :
            # Is there something solid to my left?
            if not self.checkCollision( self.turnRightTest, True ) :
                self.direction = 1
                self.walk_init()
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


    def walk_init( self ) :
        self.dy = 0
        self.dx = self.direction * 2
        self.event( "walk" + self.directionLetter() )
        self.action = self.walk  
    
    def walk( self ) :
        if self.checkForFalling() :
            return
        if self.checkForReversing() :
            return
        self.checkForStepUp()
        

    def fall_init( self ) :
        self.dx = 0
        self.dy = -1
        self.fallCount = 0
        self.event( "fall" )
        self.action = self.fall

    def fall( self ) :

        self.fallCount -= self.dy
        self.dy -= 0.2 # Accelerate a little
        if self.dy < -8 :
            self.dy = -8 # Reached terminal velocity, don't get too fast.

        # Have I hit bottom?
        if not self.checkCollision( self.fallTest ) :

            # Have I fallen too far to land safely?
            if self.fallCount > 250 :
                self.findLevel()
                self.deathEvent( "squish" )
                self.dy = 0
                self.dx = 0
            else :

                self.findLevel()

                self.action = self.walk
                if self.direction > 0 :
                    self.event( "walkR" )
                else :
                    self.event( "walkL" )
                self.dy = 0
                self.dx = self.direction * 2


    def block_init( self ) :
        self.dx = 0
        self.dy = 0
        self.addTag( "blocker" )
        self.event( "blocker" )
        self.action = self.block


    def block( self ) :
        pass


    def smash_init( self ) :
        self.dy = 0
        self.dx = self.direction # Slower than walking
        self.smashed = False # Set to true when first piece of solid is removed
        self.smashTimer = Timer.createTimerSeconds( 3 )

        
        self.smashFollower = FollowerBuilder( self.getActor() ).create()
        self.smashFollower.event( "smashed" )
        self.smashFollower.getActor().getAppearance().setAlpha( 0 )

        self.event( "smasher" + self.directionLetter() )
        self.action = self.smash

    def smash( self ) :
        if self.smashTimer.isFinished() :
            self.smashFollower.getActor().kill()
            self.smashFollower = None
            self.walk_init()
            self.walk()
        else :
            tester = self.turnLeftTest if self.direction > 0 else self.turnRightTest
            if self.smashed :
                if not self.checkCollision( tester ) :

                    self.smashFollower.getCollisionStrategy().update()
                    for solid in self.smashFollower.getCollisionStrategy().collisions( self.smashFollower.getActor(), "solid" ) :
                        self.smashSolid( solid )
                else :
                    self.smashFollower.getActor().kill()
                    self.smashFollower = None
                    self.walk_init()
            else :
                if not self.checkCollision( tester ) :
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

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


