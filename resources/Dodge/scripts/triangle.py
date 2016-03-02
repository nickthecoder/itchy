from common import * #@UnusedWildImport

import enemy

class Triangle(enemy.Enemy) :

    def tick(self):
        super(Triangle,self).tick()
        
        play = Itchy.getGame().sceneDirector
                
        if self.actor.x < 0 :
            self.actor.x += 800
            self.vx -= play.speedUp

        if self.actor.x > 800 :
            self.actor.x -= 800
            self.vx += play.speedUp
                        
        if self.actor.y < 0 :
            self.actor.y += 600
            self.vy -= play.speedUp
            
        if self.actor.y > 600 :
            self.actor.y -= 600
            self.vy += play.speedUp


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


