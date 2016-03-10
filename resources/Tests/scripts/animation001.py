from common import * #@UnusedWildImport

from uk.co.nickthecoder.itchy.animation import Eases, ScaleAnimation, MoveAnimation, ColorAnimation, ForwardsAnimation
from uk.co.nickthecoder.itchy.animation import HeadAnimation, HeadToAnimation, TurnAnimation, TurnToAnimation
from uk.co.nickthecoder.itchy.util import BeanHelper

import test

#
# Checks basic Animations.
#

animations = [ \
    ScaleAnimation(5,Eases.linear, 2 ), \
    MoveAnimation(5,Eases.linear, 0, 10 ), \
    ColorAnimation(5,Eases.linear, RGBA(255,255,0)), \
    ForwardsAnimation(5,Eases.linear, 10,0), \
    ForwardsAnimation(5,Eases.linear, 5,10), \
    HeadAnimation(5,Eases.linear, 90), \
    HeadToAnimation(5,Eases.linear, 90), \
    HeadAnimation(5,Eases.linear, 90), \
    HeadToAnimation(5,Eases.linear, 90), \
    TurnAnimation(5,Eases.linear, 90), \
    TurnToAnimation(5,Eases.linear, 90), \
    TurnAnimation(5,Eases.linear, 90), \
    TurnToAnimation(5,Eases.linear, 90), \
    ]

accesses = [ \
    "appearance.scale", \
    "y", \
    "appearance.colorize", \
    "y", \
    "x", \
    "heading", \
    "heading", \
    "heading", \
    "heading", \
    "appearance.direction", \
    "appearance.direction", \
    "appearance.direction", \
    "appearance.direction", \
    ]

expectedValues = [ \
    [ 1, 1.2, 1.4, 1.6, 1.8, 2.0 ], \
    [ 300, 302, 304, 306, 308, 310 ], \
    [ RGBA(255,255,255), RGBA(255,255,204), RGBA(255,255,153), RGBA(255,255,102), RGBA(255,255,51), RGBA(255,255,0) ], \
    [ 300, 302, 304, 306, 308, 310 ], \
    [ 210, 208, 206, 204, 202, 200 ], \
    [ 0, 18, 36, 54, 72, 90 ], \
    [ 0, 18, 36, 54, 72, 90 ], \
    [ -90, -72, -54, -36, -18, 0 ], \
    [ -90, -54, -18, 18, 54, 90 ], \
    [ 0, 18, 36, 54, 72, 90 ], \
    [ 0, 18, 36, 54, 72, 90 ], \
    [ -90, -72, -54, -36, -18, 0 ], \
    [ -90, -54, -18, 18, 54, 90 ], \
    ]
    
class Animation001(test.Test) :

    def __init__(self) :
        super(Animation001,self).__init__()
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
            
        if self.ticks == 5 :
            self.endTest()

        self.ticks += 1


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


    
