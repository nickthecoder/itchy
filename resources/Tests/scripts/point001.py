from common import * #@UnusedWildImport

import math

import test

class Point001(test.Test) :

    def __init__(self) :
        super(Point001,self).__init__()
        pass
        
    def onActivate(self) :
        print "Test", self.__module__
        
    def tick(self) :
        one = game.findActorById("one")
        two = game.findActorById("two")
        three = game.findActorById("three")
        
        self.compare( "One @ 100,100 X", 100, one.position.x )
        self.compare( "One @ 100,100 Y", 100, one.position.y )
        
        one.moveForwards( 10 )
        self.compare( "One Forwards 10 Y",  90, one.y )
        self.compare( "One Forwards 10 X", 100, one.x )

        self.compare( "One @ 100,90 X", 100, one.position.x )
        self.compare( "One @ 100,90 Y",  90, one.position.y )

        two.moveTo( one )
        self.compare( "Two @ 100,90 X", 100, two.position.x )
        self.compare( "Two @ 100,90 Y",  90, two.position.y )
        
        three.moveTo( 200, 150 )
        self.compare( "Three @ 200,150 X", 200, three.position.x )
        self.compare( "Three @ 200,150 Y", 150, three.position.y )
        
        three.moveTo( Point(300, 250) )
        self.compare( "Three @ 300,250 X", 300, three.position.x )
        self.compare( "Three @ 300,250 Y", 250, three.position.y )
        
        three.moveBy( 100, 10 )
        self.compare( "Three @ 400,260 X", 400, three.position.x )
        self.compare( "Three @ 400,260 Y", 260, three.position.y )
        
        p1 = Point( 10, 20 )
        self.compare( "p1.x", 10, p1.x )
        self.compare( "p1.y", 20, p1.y )

        p2 = p1.translate( 5, 1 )
        self.compare( "p2.x", 15, p2.x )
        self.compare( "p2.y", 21, p2.y )
        
        p3 = p1.translateDegrees( 0, 3 )
        self.compare( "p3.x", 13, p3.x )
        self.compare( "p3.y", 20, p3.y )

        p4 = p1.translateDegrees( 90, 3 )
        self.compare( "p4.x", 10, p4.x )
        self.compare( "p4.y", 23, p4.y )

        p5 = p1.translateDegrees( 30, 10 )
        self.compare( "p5.y", 25, p5.y )

        p6 = p1.translateDegrees( 60, 10 )
        self.compare( "p6.x", 15, p6.x )

        p7 = p1.translateRadians( 60.0 * math.pi / 180.0, 10 )
        self.compare( "p7.x", 15, p7.x )

        p8 = p1.translateDegrees( 0, 10, 20 )
        self.compare( "p8.x", 20, p8.x )
        self.compare( "p8.y", 40, p8.y )

        p9 = p1.translateDegrees( 90, 10, 20 )
        self.compare( "p9.x", -10, p9.x )
        self.compare( "p9.y",  30, p9.y )

        p10 = p1.translateDegrees( 60, 0, 20 )
        self.compare( "p10.y",  30, p10.y )

        p11 = p1.translateRadians( 60.0 * math.pi / 180.0, 0, 20 )
        self.compare( "p11.y",  30, p11.y )

        self.compare( "p11 - p1", 20, p11.distance( p1 ) )
        self.compare( "p6 - p1" , 10, p6.distance( p1 ) )

        self.compare( "p6.distanceSqured( p1 )" , 100, p6.distanceSquared( p1 ) )
        
        self.compare( "Direction 45", 45, Point( 1,1 ).directionDegrees( Point(3,3) ) )
        self.compare( "Direction  0",  0, Point( 1,1 ).directionDegrees( Point(3,1) ) )
        self.compare( "Direction 90", 90, Point( 1,1 ).directionDegrees( Point(1,3) ) )

        self.compare( "Direction 45", math.pi/4, Point( 1,1 ).directionRadians( Point(3,3) ) )
        self.compare( "Direction  0",         0, Point( 1,1 ).directionRadians( Point(3,1) ) )
        self.compare( "Direction 90", math.pi/2, Point( 1,1 ).directionRadians( Point(1,3) ) )

        self.compare( "Towards A", 11, p1.towards( Point( 40,20 ), 1 ).x )
        self.compare( "Towards B", 20, p1.towards( Point( 40,20 ), 1 ).y )

        self.compare( "Towards C", 40, p1.towards( Point( 40,20 ), 100 ).x ) # Don't overshoot!
        self.compare( "Towards D", 20, p1.towards( Point( 40,20 ), 100 ).y )

        self.compare( "Towards E", 13, p1.towards( Point( 16,28 ), 5 ).x )
        self.compare( "Towards F", 24, p1.towards( Point( 16,28 ), 5 ).y )

        self.endTest()

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


