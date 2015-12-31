from uk.co.nickthecoder.itchy import AbstractRole

class Moving(AbstractRole) :

    def __init__(self) :
        self.vx = 0
        self.vy = 0

    def tick(self) :
        actor = self.getActor()
        actor.moveBy(self.vx, self.vy)

