from common import * #@UnusedWildImport

from gridRole import GridRole

class Empty(GridRole) :

    def __init__( self ) :
        super(Empty,self).__init__()

        self.addTag("soft")
        self.addTag("squashable")
    
    def isEmpty( self ) :
        return True

