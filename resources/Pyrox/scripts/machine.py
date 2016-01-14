from common import *

from gridRole import GridRole, GridRoleFeatures
from dummy import Dummy

properties = ArrayList()

costumeProperties = ArrayList()
costumeProperties.add( StringProperty( "fromCostume" ) )
costumeProperties.add( StringProperty( "toCostume" ) )
costumeProperties.add( StringProperty( "enters" ) )
costumeProperties.add( StringProperty( "exits" ) )
costumeProperties.add( IntegerProperty( "offsetX" ) )
costumeProperties.add( IntegerProperty( "offsetY" ) )

class Machine(GridRole) :

    def __init__(self) :
        GridRole.__init__(self)
        self.fromCostume = None
        self.toCostume = None
        self.fromDX = 1
        self.fromDY = 0
        self.offsetX = 0
        self.offsetY = 0
        self.pregnant = False # Are we ready to give birth to an object?
        self.pulled = False # Tells onInvaded that the invading object was pulled in, and should be transmuted.
        
                            
    def onSceneCreated(self):
        GridRole.onSceneCreated(self)
        self.makeAlternateOccupant()
        self.costumeFeatures.update(self)
        
        dummy = None
        squareSize = self.square.grid.squareSize
        # Create a dummy actor if we are a wide machine
        pose = self.actor.appearance.pose
        if pose.surface.width > squareSize :
            if pose.offsetX > squareSize :
                dummy = Dummy(self, -1, 0)
            else :
                dummy = Dummy(self, 1, 0)
    
        if dummy :
            dummy.makeAlternateOccupant()        

        
    def tick(self) :
    
        if self.pregnant :
            opening = self.look( self.toDX + self.offsetX, self.toDY + self.offsetY )
            if opening.isEmpty() :
                
                resources = Itchy.getGame().resources
                    
                actor = resources.createActor( self.toCostume, self.actor.stage )
                squareSize = self.square.grid.squareSize
                actor.moveTo( self.actor.x + self.offsetX * squareSize, self.actor.y + self.offsetY * squareSize )
                actor.role.placeOnGrid( self.square.grid )
                
                self.createdRole = actor.role
                actor.role.move( self.toDX, self.toDY, self.speed )
                self.pregnant = False
            return
            
        # Is the machine already full? If so, don't pull another object in.
        if self.square.occupant is not None or self.square.entrant is not None :
            return
        
        self.considerPulling( self.look(-self.fromDX, -self.fromDY ).role )
        
        if self.offsetX != 0 or self.offsetY != 0 :
            self.considerPulling( self.look( -self.fromDX + self.offsetX, -self.fromDY + self.offsetY ).role ) 
            
    def considerPulling( self, thing ) :
    
        if thing is None :
            return
            
        if thing.isEmpty() or thing.isMoving() :
            return

        if thing.idle and thing.actor and thing.actor.costume == self.fromCostume :
            self.pulled = True
            thing.move( self.fromDX, self.fromDY, self.speed )
            thing.event("pull")

    def onInvaded( self, invader ) :
        if self.pulled :
            invader.actor.kill()
            self.pregnant = True
            self.pulled = False
        

    def createCostumeFeatures(self,costume) :
        return MachineFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Role, self.__module__ + ".py" )



class MachineFeatures(GridRoleFeatures) :

    def __init__(self,costume) :
        super(MachineFeatures,self).__init__(costume)
        self.fromCostume = "carR"
        self.toCostume = "carL"
        self.enters = "E"
        self.exits = "W"
        self.offsetX = 0;
        self.offsetY = 0;

    def update( self, role ) :
    
        resources = Itchy.getGame().resources

        role.fromCostume = resources.getCostume(self.fromCostume)
        role.toCostume = resources.getCostume(self.toCostume)

        role.offsetX = self.offsetX
        role.offsetY = self.offsetY
        
        if self.enters == "E" :
            role.fromDX = 1
            role.fromDY = 0
            
        if self.enters == "W" :
            role.fromDX = -1
            role.fromDY = 0
            
        if self.enters == "N" :
            role.fromDX = 0
            role.fromDY = 1
            
        if self.enters == "S" :
            role.fromDX = 0
            role.fromDY = -1
    
    
        if self.exits == "E" :
            role.toDX = 1
            role.toDY = 0
            
        if self.exits == "W" :
            role.toDX = -1
            role.toDY = 0
            
        if self.exits == "N" :
            role.toDX = 0
            role.toDY = 1
            
        if self.exits == "S" :
            role.toDX = 0
            role.toDY = -1
            
    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties


