import math

from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.role import OnionSkin
from uk.co.nickthecoder.itchy.role import Explosion
from uk.co.nickthecoder.itchy.extras import Timer
from uk.co.nickthecoder.itchy.extras import Fragment

from java.util import ArrayList
from moving import Moving

properties = ArrayList()

class Bullet(Moving) :

    def __init__(self) :
        Moving.__init__(self)
        self.speed = 1

    def tick(self) :
    	Moving.tick(self)
        
        i = Itchy.getGame().getDirector().collisionStrategy.collisions(self.getActor(),"shootable").iterator()
        # i = self.getActor().pixelOverlap("shootable").iterator();
        # Shoot ONE of the rocks that the bullet is touching.
        if i.hasNext() :
        	role = i.next()
        	if not role.getActor().isDying() :
        		role.shot(self)
        		self.getActor().kill()

    def onMessage(self, message) :
    	if message == "die" :
    		# Sent at the end of the fade out animation. Its the animation that determines the bullets max life span.
    		self.getActor().kill()


