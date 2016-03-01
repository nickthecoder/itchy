from common import * #@UnusedWildImport

properties = ArrayList()
properties.add( DoubleProperty( "vx" ).aliases("dx") )
properties.add( DoubleProperty( "vy" ).aliases("dy") )
properties.add( DoubleProperty( "rotation" ) )

class Enemy(AbstractRole) :

    def __init__(self) :
        self.vx = 0
        self.vy = 0
        self.rotation = 0
        self.addTag("deadly")

        
    def tick(self):
        play = Itchy.getGame().sceneDirector
        width = self.actor.appearance.width
        height = self.actor.appearance.height
        
        self.actor.x += self.vx
        self.actor.y += self.vy
        self.actor.direction += self.rotation
        
        if self.actor.x < width / 2 and self.vx < 0:
            self.vx = -self.vx + play.speedUp
            #self.rotation = - self.rotation
            
        if self.actor.x > 800 - width / 2 and self.vx > 0:
            self.vx = -self.vx - play.speedUp
            #self.rotation = - self.rotation
        
        if self.actor.y < height / 2 and self.vy < 0:
            self.vy = -self.vy + play.speedUp
            #self.rotation = - self.rotation
       
        if self.actor.y > 600 - height / 2 and self.vy > 0:
            self.vy = -self.vy - play.speedUp
            #self.rotation = - self.rotation



    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


