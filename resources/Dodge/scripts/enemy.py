from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "dx" ) )
properties.add( DoubleProperty( "dy" ) )

class Enemy(AbstractRole) :

    def __init__(self) :
        self.dx = 0
        self.dy = 0
        self.addTag("deadly")
        
    def tick(self):
        play = Itchy.getGame().sceneDirector
        
        self.actor.x += self.dx
        self.actor.y += self.dy
        
        if self.actor.x < 0 :
            self.dx = -self.dx + play.speedUp
            self.actor.x += self.dx
            
        if self.actor.x > 800 :
            self.dx = -self.dx - play.speedUp
            self.actor.x += self.dx
        
        if self.actor.y < 0 :
            self.dy = -self.dy + play.speedUp
            self.actor.y += self.dy
       
        if self.actor.y > 600 :
            self.dy = -self.dy - play.speedUp
            self.actor.y += self.dy


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


