from common import *

properties = ArrayList()
properties.add( StringProperty( "job" ) )

class JobButton(ButtonRole) :

    def __init__(self) :
        self.job = "blocker"
        
    def onBirth(self):
        self.event( self.job )

    def onAttach(self):
        pass

    def tick(self):
        pass

    def onClick(self) :
        print "Job : ", self.job
        Itchy.getGame().sceneDirector.pickJob( self )

    # TODO Other methods include :
    # onDetach, onKill, onMouseDown, onMouseUp, onMouseMove

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


