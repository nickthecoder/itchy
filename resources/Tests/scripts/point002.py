from common import * #@UnusedWildImport

import math

import test

class Point002(test.Test) :

    def __init__(self) :
        super(Point002,self).__init__()
        pass
        
    def onActivate(self) :
        print "Test", self.__module__
        
    def tick(self) :
        one = game.findActorById("one")
        two = game.findActorById("two")
        three = game.findActorById("three")
        
        self.compare( "One @ 100,100 X", 100, one.position.x )
        self.compare( "One @ 100,100 Y", 100, one.position.y )
        
        one.position.moveDegrees( -90, 10 )
        self.compare( "One Forwards 10 Y",  90, one.y )
        self.compare( "One Forwards 10 X", 100, one.x )

        self.compare( "One @ 100,90 X", 100, one.position.x )
        self.compare( "One @ 100,90 Y",  90, one.position.y )

        two.position.moveTo( one.position )
        self.compare( "Two @ 100,90 X", 100, two.position.x )
        self.compare( "Two @ 100,90 Y",  90, two.position.y )
        
        three.position.moveTo( 200, 150 )
        self.compare( "Three @ 200,150 X", 200, three.position.x )
        self.compare( "Three @ 200,150 Y", 150, three.position.y )
        
        three.position.moveTo( Point(300, 250) )
        self.compare( "Three @ 300,250 X", 300, three.position.x )
        self.compare( "Three @ 300,250 Y", 250, three.position.y )
        
        three.position.moveBy( 100, 10 )
        self.compare( "Three @ 400,260 X", 400, three.position.x )
        self.compare( "Three @ 400,260 Y", 260, three.position.y )
        
        p1 = Point( 10, 20 )
        
        p2 = Point( p1 )
        p2.moveBy( 5, 1 )
        self.compare( "p2.x", 15, p2.x )
        self.compare( "p2.y", 21, p2.y )
        
        p3 = Point( p1 )
        p3.moveDegrees( 0, 3 )
        self.compare( "p3.x", 13, p3.x )
        self.compare( "p3.y", 20, p3.y )

        p4 = Point( p1 )
        p4.moveDegrees( 90, 3 )
        self.compare( "p4.x", 10, p4.x )
        self.compare( "p4.y", 23, p4.y )

        p5 = Point( p1 )
        p5.moveDegrees( 30, 10 )
        self.compare( "p5.y", 25, p5.y )

        p6 = Point( p1 )
        p6.moveDegrees( 60, 10 )
        self.compare( "p6.x", 15, p6.x )

        p7 = Point( p1 )
        p7.moveRadians( 60.0 * math.pi / 180.0, 10 )
        self.compare( "p7.x", 15, p7.x )

        p8 = Point( p1 )
        p8.moveDegrees( 0, 10, 20 )
        self.compare( "p8.x", 20, p8.x )
        self.compare( "p8.y", 40, p8.y )

        p9 = Point( p1 )
        p9.moveDegrees( 90, 10, 20 )
        self.compare( "p9.x", -10, p9.x )
        self.compare( "p9.y",  30, p9.y )

        p10 = Point( p1 )
        p10.moveDegrees( 60, 0, 20 )
        self.compare( "p10.y",  30, p10.y )

        p11 = Point( p1 )
        p11.moveRadians( 60.0 * math.pi / 180.0, 0, 20 )
        self.compare( "p11.y",  30, p11.y )

        
        p20 = Point(p1)
        p20.moveTowards( Point( 40,20 ), 1 )
        self.compare( "Towards A", 11, p20.x )
        self.compare( "Towards B", 20, p20.y )

        p21 = Point(p1)
        p21.moveTowards( Point( 40,20 ), 100 )
        self.compare( "Towards C", 40, p21.x ) # Don't overshoot!
        self.compare( "Towards D", 20, p21.y )

        p22 = Point(p1)
        p22.moveTowards(Point( 16,28 ), 5 )
        self.compare( "Towards E", 13, p22.x )
        self.compare( "Towards F", 24, p22.y )

        self.endTest()

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


