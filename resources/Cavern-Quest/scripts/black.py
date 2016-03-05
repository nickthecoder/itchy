from common import * #@UnusedWildImport

properties = ArrayList()

# Used to obscure the scene when the scene is first loaded.
# These black objects are then removed from top left to bottom right.
# This is soely to mimic the origian game's appearance.
class Black(AbstractRole) :

    def __init__(self) :
        pass
        
    def onBirth(self):
        delay = 0
        squareSize = Itchy.getGame().sceneDirector.grid.squareSize
        for y in range(0,17) :
            for x in range(0,22) :
                actor = Actor( self.actor.appearance.pose )
                actor.moveTo( self.actor.x + x * squareSize, self.actor.y - y * squareSize )
                self.actor.stage.add( actor )
                actor.role = OneBlack(delay)
                delay += 0.0092

        self.actor.kill()
        

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


class OneBlack(AbstractRole) :

    def __init__(self, delay) :
        super(OneBlack, self).__init__()
        self.timer = Timer.createTimerSeconds(delay)
        
    def tick(self) :
        if self.timer.isFinished() :
            self.actor.kill()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, "oneBlack.py" )


