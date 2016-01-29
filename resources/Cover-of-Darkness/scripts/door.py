from common import *


properties = ArrayList()

class Door(AbstractRole) :

    def __init__(self) :
        self.rotate = 0
        self.rotateSpeed = 2
        self.open = False
        self.locked = False
        self.angle = 96
        
    def onBirth(self):
        self.addTag("solid")
        self.addTag("opaque")
        self.addTag("door")

    def tick(self):
        oldDirection = self.actor.heading
        if self.rotate != 0 :

            self.actor.direction += self.rotateSpeed if self.open > 0 else -self.rotateSpeed

            if self.collided( "guard" ) :
                game.director.restartScene()
        
            if self.collided( "player" ) :
                self.actor.direction = oldDirection
            else :
                self.rotate -= self.rotateSpeed                            

    def unlock( self ) :
        self.locked = False
        self.click()
    
    def click( self ) :
        if self.locked :
            return
        
        if self.rotate != 0 :
            return
            
        self.addTag("clickable")
        self.rotate = 96
        self.open = not self.open

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


