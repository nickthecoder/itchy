from common import *
from ming import *

MAX_SAFE_HEIGHT = 200

class Job() :

    def start(self, ming) :
        pass

    def tick( self, ming ) :
    
        if ming.checkForFalling() :
            return

        if ming.actor.x != ming.prevX or ming.actor.y != ming.prevY:
            if ming.checkForReversing() :
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


class Walker(Job) :

    def start(self, ming) :
        print "Walker", ming.directionLetter()
        ming.event( "walk" + ming.directionLetter() )
    
    def tick( self, ming ) :

        ming.findLevel()
        Job.tick( self, ming )
        
        
class Faller(Job) :
    
    def start( self, ming ) :
        print "Faller"
        self.fallStart = ming.actor.y # Used to calculate the height of the drop.
        ming.event( "fall" )

    def assignJob( self, ming, jobName ) :
        # We can only give umbrellas to a faller. No other jobs assignments are allowed.
        if jobName != "floater" :
            return False;
        return Job.assignJob( ming, jobName )

    def tick( self, ming ) :

        # Have I hit bottom?
        if ming.somethingBelow() :
            print "Stopped falling"
            
            ming.findLevel()
            # Have I fallen too far to land safely?
            if self.fallStart - ming.actor.y > MAX_SAFE_HEIGHT :
                ming.deathEvent( "splat" )

            else :
                ming.changeJob( Walker() )


class Floater(Job) :

    def start( self, ming ) :
        print "Floater"
        ming.event( "float" )

    def assignJob( self, ming, jobName ) :
        # Do the same logic as for Faller.
        # Can we waste an umbrella is handled in just one place ( Job.assignJob )
        if jobName != "floater" :
            return False;
        return Job.assignJob( ming, jobName )

    def tick(self, ming) :
        
        # Have I hit bottom?
        if ming.somethingBelow() :
            print "Stopped floating"
            ming.findLevel()
            ming.changeJob( Walker() )


class Builder(Job) :

    def start( self, ming ) :
        print "Builder"
        ming.event( "builder" + ming.directionLetter() )
    
    def onMessage( self, ming, message ) :
        if message == "laidBrick" :
            brick = ming.actor.createCompanion( "brick" )
            brick.moveBy( ming.direction * 3 * PIXELATION_SIZE, 0 )
        if message == "jobComplete" :
            ming.changeJob( Walker() )


class Blocker(Job) :
    
    def start( self, ming ) :
        print "Blocker"
        ming.collisionStrategy.update()
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
            if ming.somethingInfront() :
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

    def onMessage( self, ming, message ) :
        print "Digger message", message
        
        if message == "jobComplete" :
            ming.changeJob( Walker() )
        if message == "dig" :
            ming.removeSolids( self.remover )

    def quit( self, ming ) :
        self.remover.actor.kill()

        
class Bomber(Job) :

    def start( self, ming ) :
        ming.deathEvent( "bomb" )

    def onMessage( self, ming, message ) :
        if message == "bang" :
            remover = ming.createRemover( "crater" )
            ming.removeSolids( remover )
            remover.actor.kill()

            ExplosionBuilder(ming.actor) \
                .fragments( ming.costumeFeatures.fragments ) \
                .speed(0.5,0.5,0.5,0.5).fade(2).vy(5).vx(-1,1).gravity(-0.1) \
                .create()



