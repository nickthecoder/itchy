from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "dx" ) )
properties.add( DoubleProperty( "dy" ) )
properties.add( DoubleProperty( "rotation" ) )

class Enemy(AbstractRole) :

    def __init__(self) :
        self.dx = 0
        self.dy = 0
        self.rotation = 0
        self.addTag("deadly")

        
    def tick(self):
        play = Itchy.getGame().sceneDirector
        width = self.actor.appearance.width
        height = self.actor.appearance.height
        
        self.actor.x += self.dx
        self.actor.y += self.dy
        self.actor.direction += self.rotation
        
        if self.actor.x < width / 2 and self.dx < 0:
            self.dx = -self.dx + play.speedUp
            #self.rotation = - self.rotation
            
        if self.actor.x > 800 - width / 2 and self.dx > 0:
            self.dx = -self.dx - play.speedUp
            #self.rotation = - self.rotation
        
        if self.actor.y < height / 2 and self.dy < 0:
            self.dy = -self.dy + play.speedUp
            #self.rotation = - self.rotation
       
        if self.actor.y > 600 - height / 2 and self.dy > 0:
            self.dy = -self.dy - play.speedUp
            #self.rotation = - self.rotation



    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


