from common import * #@UnusedWildImport

import enemy

class Rectangle(enemy.Enemy) :

    def tick(self):

        super(Rectangle, self).tick()
        
        play = Itchy.getGame().sceneDirector
        width = self.actor.appearance.width
        height = self.actor.appearance.height
        
        if self.actor.x < width / 2 and self.vx < 0:
            self.vx = -self.vx + play.speedUp
            
        if self.actor.x > 800 - width / 2 and self.vx > 0:
            self.vx = -self.vx - play.speedUp
        
        if self.actor.y < height / 2 and self.vy < 0:
            self.vy = -self.vy + play.speedUp
       
        if self.actor.y > 600 - height / 2 and self.vy > 0:
            self.vy = -self.vy - play.speedUp


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


