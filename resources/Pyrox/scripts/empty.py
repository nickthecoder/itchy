from common import *

from gridRole import GridRole

class Empty(GridRole) :

    def __init__( self ) :
        super(Empty,self).__init__()

        self.addTag("soft")
        self.addTag("enemySoft")
        self.addTag("squashE")
        self.addTag("squashS")
        self.addTag("squashW")
        self.addTag("squashN")
    
    def isEmpty( self ) :
        return True

