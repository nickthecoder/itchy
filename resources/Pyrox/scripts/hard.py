from common import *

from gridRole import GridRole

properties = ArrayList()

class Hard(GridRole) :

    def __init__(self) :
        super(Hard,self).__init__()
        self.hardness = 30
                
    def onBirth(self) :
        super(Hard,self).onBirth()
        self.remaining = self.hardness

    def canShove( self, pusher, dx, dy, speed, force ) :
    
        if pusher.hasTag("digger") and not self.actor.isDying() :
            
            self.actor.appearance.alpha -= 128 / self.hardness

            ExplosionBuilder(self.actor) \
                .projectiles(1) \
                .gravity(-0.2).fade(0.9, 3.5).speed(0.1, 1.5).vy(5) \
                .pose("fragment") \
                .create()

            self.remaining -= 1
            if self.remaining <= 0 :
                self.actor.kill()

        return self.remaining == 0

    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


