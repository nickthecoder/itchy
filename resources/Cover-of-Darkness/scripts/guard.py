from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName

from java.util import ArrayList

from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import DoubleProperty

properties = ArrayList()
properties.add( DoubleProperty( "forwardSpeed" ) )
properties.add( DoubleProperty( "turnSpeed" ) )
properties.add( StringProperty( "routeString"  ) )

# routeString is a set of parts separated by spaces.
# A part is either a number, or a command letter followed by a number.
# The command letters are as follows:
# L - Turn left n degrees.
# R - Turn right n degrees.
# <no command> - Move forwards n pixels
# C - Move in an arc n pixels, where the amount of turn is given in the NEXT part,
#     which should be either an L or a R command.

class Guard(AbstractRole) :

    def __init__(self) :
        self.routeString = "L360 R360"
        self.forwardSpeed = 1
        self.turnSpeed = 1
        
        self.steps = 0
        self.stepsTaken = 0
        self.routeIndex = 0
        
        self.turnAmount = 1
        self.forwardAmount = 0
        
    def onBirth(self) :
        self.addTag( "guard" )

    def tick(self) :
    
        if self.stepsTaken >= self.steps :
            self.nextPart()
            
            # The "C" command needs a bodge to keep it symetric. So we make a half step forward at the beginnging
            # and a half step at the end without a turn.
            if self.turnAmount != 0 and self.forwardAmount != 0 :
                self.steps += 1        

        self.stepsTaken += 1

        if self.turnAmount != 0 and self.forwardAmount != 0 and self.stepsTaken == 1 :
            # Do the half step at the beginning.
            self.actor.moveForwards( self.forwardAmount / 2)
            
        elif self.turnAmount != 0 and self.forwardAmount != 0 and self.stepsTaken == self.steps :
             # Half step at the end WITHOUT a turn after it.
             self.actor.moveForwards( self.forwardAmount / 2)
             return
        else :
            # Normal step
            if self.forwardAmount != 0 :
                self.actor.moveForwards( self.forwardAmount )

        # Turn
        if self.turnAmount != 0 :
            self.actor.setDirection( self.actor.getDirection() + self.turnAmount )            


    def nextPart(self) :
        self.stepsTaken = 0
        parts = self.routeString.split();

        if self.routeIndex >= len( parts ) :
            self.routeIndex = 0
        
        part = parts[ self.routeIndex ]
        self.routeIndex += 1

        #try :
        command = part[0:1].upper()
        value = float( part[1:] ) if command.isalpha() else float( part )
        
        if command == "L" or command == "R" :
            # Calculate the number of steps needed, and the how far round each step has to be.       
            # turnAmount will be close to, but not always exactly the same as self.turnSpeed.
            self.steps = int( value / self.turnSpeed )
            self.turnAmount = value / self.steps
            self.forwardAmount = 0

            if command == "R":
                self.turnAmount = - self.turnAmount
        
        elif command == "S" :
            self.steps = int(value * 60)
            self.forwardAmount = 0
            self.turnAmount = 0

        elif command == "T" :
            self.steps = int(value)
            self.forwardAmount = 0
            self.turnAmount = 0
            
        else :
            # Calculate the number of steps needed, and then how far each step has to be.
            # forwardAmount will be close to, but not always exactly the same as self.speed.
            self.steps = int( value / self.forwardSpeed )
            self.forwardAmount = value / self.steps
            self.turnAmount = 0

            # Command C is a CURVE - use the next part to work out the angle to turn and this part for the length,
            # and do them simultaneously.
            if len(parts) > 1 and command == "C" :
                tmpSteps = self.steps
                self.nextPart()
                self.turnAmount = self.turnAmount * self.steps / tmpSteps
                self.steps = tmpSteps
                self.forwardAmount = value / self.steps

        #except :
        #    print "Guard skipping", part
            
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


