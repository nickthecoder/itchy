from common import *

from gridRole import GridRole

game = Itchy.getGame()

properties = ArrayList()
properties.add( StringProperty( "requires" ) )
properties.add( StringProperty( "text" ) )

class Portcullis(GridRole) :

    def __init__(self) :
        super(Portcullis,self).__init__()
        self.requires = ""
        self.text = ""
        self.timer = None

    def onAttach(self) :
        super(Portcullis,self).onAttach()
        self.addTag("portcullis")

    def getReady(self, player) :
        delay = player.actor.distanceTo( self.actor ) / 200
        if self.isOpen() :
            self.timer = Timer.createTimerSeconds( delay )
            
    def tick(self) :
        if self.timer and self.timer.isFinished() :
            self.explode()
            self.actor.kill()

    def isOpen(self):
        node = game.preferences.node("completed")
        for requirement in self.requires.split(",") :
        
            if not node.getBoolean( requirement.strip(), False ) :
                return False

        return True

    def canShove(self,shover, dx, dy, speed, force ) :
        if shover.hasTag("player") :
            req = self.requires if self.text == "" else self.text
            shover.talk( self.actor.costume.getString("requires") + "\n" + req )
    
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


