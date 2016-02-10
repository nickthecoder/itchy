from common import * #@UnusedWildImport

properties = ArrayList()
# TODO declare properties here. Note that you must also initialise them in __init__
# e.g.
# properties.add( StringProperty( "myString" ).hint("My green label" ) )

class Alien(AbstractRole) :

    def __init__(self) :
        self.testValue = 0.0
        pass
        
    def onBirth(self):
        pass

    def tick(self):
        if self.testValue != 0 :
            self.actor.direction = self.testValue
            self.actor.appearance.alpha = self.testValue 


    # TODO Other methods include :
    # onSceneCreated, onDetach, onKill, onMouseDown, onMouseUp, onMouseMove, createCostumeProperties

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


