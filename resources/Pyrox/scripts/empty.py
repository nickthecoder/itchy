from gridRole import GridRole

from uk.co.nickthecoder.itchy import Itchy

class Empty(GridRole) :

    def __init__( self ) :
        super(Empty,self).__init__()

        self.addTag("soft")
        self.addTag("squashE")
        self.addTag("squashS")
        self.addTag("squashW")
        self.addTag("squashN")

    def pushed( self, pusher, dx, dy, force) :
        return True

    
    def isEmpty( self ) :
        return True

