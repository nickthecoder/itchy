from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import Role
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy.util import ClassName
from uk.co.nickthecoder.itchy.extras import Timer

from java.util import ArrayList
from java.util import Random

from uk.co.nickthecoder.itchy.property import StringProperty
from uk.co.nickthecoder.itchy.property import DoubleProperty
from uk.co.nickthecoder.itchy.property import BooleanProperty

properties = ArrayList()
properties.add( StringProperty( "text" ).multiLine() )
properties.add( DoubleProperty( "initialDelay" ).hint("seconds") )
properties.add( DoubleProperty( "period" ).hint("seconds") )
properties.add( BooleanProperty( "loop" ) )

# If you don't know why this is called Sledger, sledging is a cricketing phrase, meaning :
# To verbally insult or abuse an opponent in order to distract them.
# So, use this to add comments, hopefully humourous, that will distract the player, and may
# sometimes be helpful (but I expect I'll use Hint, if I want to actually HELP).
# After writing this code, I remembered a game called Fish Fillets, which has sledging in it.

class Sledger(AbstractRole) :

    def __init__(self) :
        self.text=""
        self.initialDelay = 0.5
        self.period = 15
        self.loop = False

        self.index = 0
        self.timer = None

    def onBirth(self) :
        if self.text == "" :
            self.actor.kill()
        else :
            self.choices = self.text.split("\n");
            self.timer = Timer.createTimerSeconds( self.initialDelay )
            self.actor.appearance.alpha = 0
    
    def tick(self) :

        if self.timer and self.timer.isFinished() :
            self.timer = Timer.createTimerSeconds( self.period )
            self.talk()
            
    def talk(self) :

        player = Itchy.getGame().sceneDirector.player

        if player :
            text = self.choices[ self.index ]

            if not text.strip() == "" :
                player.talk( text )
                
            self.index += 1
            if self.index >= len(self.choices) :
                self.index = 0
                if not self.loop :
                    self.timer = None
                    self.actor.kill()


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


