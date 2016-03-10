from common import * #@UnusedWildImport

from uk.co.nickthecoder.itchy.animation import Eases, MoveAnimation, CompoundAnimation
from uk.co.nickthecoder.itchy.util import BeanHelper

import test
#
# Checks Parallel and Sequence animations (CompoundAnimation).
#
seq = CompoundAnimation( True )
seq.add( MoveAnimation(5,Eases.linear, 0, 10 ) )
seq.add( MoveAnimation(5,Eases.linear, 10, 0 ) )

para = CompoundAnimation( False )
para.add( MoveAnimation(5,Eases.linear, 0, 10 ) )
para.add( MoveAnimation(5,Eases.linear, 10, 0 ) )

animations = [ seq, seq, para, para ]
    
accesses = [ "x", "y", "x", "y" ]
    
expectedValues = [ \
    [ 50,  50, 50, 50, 50, 50, 52, 54, 56, 58, 60 ], \
    [ 300,302,304,306,308,310,310,310,310,310,310 ], \
    [ 130,132,134,136,138,140,140,140,140,140,140 ], \
    [ 300,302,304,306,308,310,310,310,310,310,310 ], \
    ]
    
class Animation002(test.Test) :

    def __init__(self) :
        super(Animation002,self).__init__()
        self.ticks = 0

    def onActivate(self) :
        print "Test", self.__module__
        self.beanHelpers = []

        for i in range( 0, len(animations) ) :
            actor = game.findActorById( str(i) )
            animation = animations[i]
            actor.setAnimation( animation )
            self.beanHelpers.append( BeanHelper( actor, accesses[i] ) )
        
    
    def tick(self) :
        
        for i in range( 0, len(animations) ) :
            value = self.beanHelpers[i].get()
            self.compare( i, expectedValues[i][self.ticks], value )
            
        if self.ticks == 10 :
            self.endTest()

        self.ticks += 1

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


