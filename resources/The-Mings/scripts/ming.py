from common import *

from director import PIXELATION_SIZE

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

        self.addTag( "ming" )

        self.lookLeftRight = self.createLooker( "lookLeftRight" )
        self.lookDown = self.createLooker( "lookDown" )
        self.lookStepUp = self.createLooker( "lookStepUp" )

        self.job = Walker()
        self.job.start(self)
        
        self.job.assignJob( self, self.initialJob )
        
        
    def tick(self):

        self.job.work( self )
        self.collisionStrategy.update()


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
        
        result.actor.appearance.alpha = 100
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
            

    def reverse( self ) :
        print "Reverse!"
        self.direction = - self.direction
        self.lookLeftRight.event( "look" + self.directionLetter() )
        self.lookStepUp.event( "look" + self.directionLetter() )


    def look( self, looker, tags = ["solid"] ) :
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
        followerPose.surface.blit( rect, surface, int(tx), int(ty), Surface.BlendMode.RGBA_MULT )
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

    def assignJob( self, ming, jobName ) :

        newJob = None
        if jobName == "blocker" :
            newJob = Blocker()
            
        elif jobName == "smasher" :
            newJob = Smasher()

        elif jobName == "builder" :
            newJob = Builder()

        elif jobName == "digger" :
            newJob = Digger()

        elif jobName == "bomber" :
            newJob = Bomber()

        elif jobName == "floater" :
            # What should happen if we give two umbrellas? Silently accept or return False?
            # Already has an umbrella?
            #if ming.umbrella :
            #    return False
            ming.umbrella = True
            return True

        if newJob is not None :
            ming.changeJob( newJob )
            return True
            
        return False

    def quit( self, ming ) :
        pass    
        
    def onMessage( self, ming, message ) :
        pass    


class Stop(Job) :

    def start(self, ming) :
        print "Stop"
        
    def work(self, ming) :
        pass

class Walker(Job) :

    def start(self, ming) :
        print "Walker", ming.directionLetter()
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
        self.fallStart = ming.actor.y
        ming.event( "fall" )

    def assignJob( self, ming, jobName ) :
        if jobName != "floater" :
            return False;
        return Job.assignJob( ming, jobName )

    def work( self, ming ) :

        # Have I hit bottom?
        if ming.look( ming.lookDown ) :
            print "Stopped falling"
            
            ming.findLevel()
            # Have I fallen too far to land safely?
            if self.fallStart - ming.actor.y > 150 :
                ming.deathEvent( "splat" )

            else :
                ming.changeJob( Walker() )

class Floater(Job) :

    def start( self, ming ) :
        print "Floater"
        ming.event( "float" )

    def assignJob( self, ming, jobName ) :
        return False;
                    
    def canChange( self ) :
        return False

    def work(self, ming) :
        
        # Have I hit bottom?
        if ming.look( ming.lookDown ) :
            print "Stopped floating"
            ming.findLevel()
            ming.changeJob( Walker() )

class Builder(Job) :

    def start( self, ming ) :
        print "Builder"
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
        ming.addTag( "blocker" )
        ming.event( "blocker" )

    def quit( self, ming ) :
        ming.removeTag( "blocker" )


class Smasher(Job) :

    def start( self, ming ) :
        print "Smasher"
        self.freeSmashes = 2 # Can smash in thin air twice, before giving up

        self.remover = ming.createRemover("smash")
        ming.event( "smash" + ming.directionLetter() )

    def onMessage( self, ming, message ) :
        print "Smasher message", message

        if message == "jobComplete" :
            ming.changeJob( Walker() )
        
        if message == "smash" :
            if ming.look( ming.lookLeftRight ) :
                self.freeSmashes = 0
                ming.removeSolids( self.remover )
            else :
                if self.freeSmashes > 0 :
                    self.freeSmashes -= 1
                else :
                    # We've shashed thin air, let's not be a smasher any more.
                    ming.changeJob( Walker() )

    def quit( self, ming ) :
        self.remover.actor.kill()

class Digger(Job) :

    def start( self, ming ) :
        print "Digger"
        self.removed = False # Set to true when first piece of solid is removed

        self.remover = ming.createRemover( "dig" )
        ming.event( "digger" )

    def work( self, ming ) :
        pass

    def onMessage( self, ming, message ) :
        print "Digger message", message
        
        if message == "jobComplete" :
            ming.changeJob( Walker() )
        if message == "dig" :
            ming.removeSolids( self.remover )
            if not ming.look( ming.lookDown ) :
                ming.changeJob( Walker() )

    def quit( self, ming ) :
        self.remover.actor.kill()
        
class Bomber(Job) :

    def start( self, ming ) :
        ming.deathEvent( "bomb" )
        
    def work( self, ming ) :
        pass

    def onMessage( self, ming, message ) :
        if message == "bang" :
            remover = ming.createRemover( "crater" )
            ming.removeSolids( remover )
            remover.actor.kill()

            
