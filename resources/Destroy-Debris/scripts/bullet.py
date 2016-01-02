from common import *

from moving import Moving

properties = ArrayList()

class Bullet(Moving) :

    def __init__(self) :
        Moving.__init__(self)
        self.speed = 1

    def tick(self) :
    	Moving.tick(self)
        
        for role in self.collisions("shootable") :
            if not role.getActor().isDying() :
                role.shot(self)
                self.getActor().kill()
                return
        
    def onMessage(self, message) :
    	if message == "die" :
    		# Sent at the end of the fade out animation. Its the animation that determines the bullets max life span.
    		self.getActor().kill()


