from uk.co.nickthecoder.itchy import AbstractRole

class Moving(AbstractRole) :

    def __init__(self) :
        self.vx = 0
        self.vy = 0

    def tick(self) :
        actor = self.getActor()
        actor.moveBy(self.vx, self.vy)
        
        if (actor.getX() < -10) :
            actor.moveBy(820,0)

        if (actor.getX() > 810) :
            actor.moveBy(-820,0)

        if (actor.getY() < -10) :
            actor.moveBy(0,620)

        if (actor.getY() > 610) :
            actor.moveBy(0,-620);

