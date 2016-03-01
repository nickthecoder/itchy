from common import * #@UnusedWildImport
import math
import enemy

properties = ArrayList()
properties.addAll( enemy.properties )

class Ball(enemy.Enemy) :

    def __init__(self) :
        super(Ball,self).__init__()
        self.addTag("ball")
        
    def tick(self):
        super(Ball,self).tick()
        for other in self.collisions("ball") :
            self.bounce( other )


    def bounce( self, other ) :

        dx = self.actor.x - other.actor.x
        dy = self.actor.y - other.actor.y

        dist = math.sqrt(dx * dx + dy * dy)

        dvx = other.vx - self.vx;
        dvy = other.vy - self.vy;

        # The speed of the collision in the direction of the line between their centres.
        collision = (dvx * dx + dvy * dy) / dist

        if collision < 0 :
            # They are moving away from each other
            return

        # Mass is proportional to the square of the widths
        massA = self.actor.appearance.width * self.actor.appearance.width
        massB = other.actor.appearance.width * other.actor.appearance.width

        massSum = massA + massB

        self.vx += dx / dist * collision * 2 * massB / massSum
        other.vx -= dx / dist * collision * 2 * massA / massSum

        self.vy += dy / dist * collision * 2 * massB / massSum
        other.vy -= dy / dist * collision * 2 * massA / massSum


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


