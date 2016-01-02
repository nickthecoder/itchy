from common import *

properties = ArrayList()

class Highlight(Follower) :

    def __init__(self, parent) :
        Follower.__init__( self, parent )

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )


