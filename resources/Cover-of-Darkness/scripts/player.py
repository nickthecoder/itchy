from common import *

properties = ArrayList()
properties.add( DoubleProperty( "speed" ) )

class Player(AbstractRole) :

    def __init__(self) :
        self.speed = 1
        self.collected = 0

    def onBirth(self) :
        self.addTag("player")
        self.inputClick = Input.find("click")

    def onSceneCreated(self) :

        self.view = game.layout.findView("middle")
        
    def tick(self):

        oldX = self.actor.x
        oldY = self.actor.y
        oldDirection = self.actor.direction

        mouseX = self.view.getWorldX( Itchy.getMouseX() )
        mouseY = self.view.getWorldY( Itchy.getMouseY() )

        distance = self.actor.distanceTo( mouseX, mouseY )
        if distance > 10 :
            self.actor.direction = self.actor.directionOf( mouseX, mouseY )
            self.actor.moveForwards( self.speed if distance > 100 else self.speed * distance / 100 )

            if self.inputClick.pressed() :
                if self.collided( "clickable" ) :
                    self.collisions( "clickable" ).get(0).click()

            if self.collided("solid") :
                self.actor.direction = oldDirection
                # Hit a wall, so let's try moving x and y independantly
                tx = self.actor.x
                self.actor.moveTo( oldX, self.actor.y )
                if self.collided("solid") :
                    self.actor.moveTo( tx, oldY )
                    if self.collided("solid") :
                        # Give up, move back.
                        self.actor.moveTo(oldX, oldY)


        screenX = self.actor.x - self.view.visibleRectangle.x
        screenY = self.actor.y - self.view.visibleRectangle.y

        if screenX < 200 :
            game.sceneDirector.scrollBy( -1 + self.actor.x - oldX, 0 )
        if screenX > 600 :
            game.sceneDirector.scrollBy( 1 + self.actor.x -oldX, 0 )
            
        if screenY < 150 :
            game.sceneDirector.scrollBy( 0, -4 )
        if screenY > 450 :
            game.sceneDirector.scrollBy( 0, 4 )
            
        if self.inputClick.pressed() :
            if self.collided( "clickable" ) :
                self.collisions( "clickable" ).get(0).click()  

        if self.collided("light") :
            game.sceneDirector.caught()
            return
            
        if self.collided("exit") :

            exit = self.collisions("exit").get(0)
            if self.collected >= game.sceneDirector.collectables :
                game.director.startScene( exit.nextLevel )

        for collectable in self.collisions("collectable") :
            self.collected += collectable.collect()


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


