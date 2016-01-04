from common import *

from square import Square
from gridRole import GridRole

class GridStage(ZOrderStage) :

    def __init__(self, name) :
        ZOrderStage.__init__(self, name)
        self.grid = None
        # Used to ensure that each GridRole is never ticked twice in one frame.
        # Each GridRole has a "lastTicked", and if this is == this.tickCount, then don't tick.
        self.tickCount = 0

    # In this strategy game, we want PREDICTABLE behaviour, and the standard ZOrderStage doesn't give us that.
    # In ZOrderStage, the ticks are done in z-order, but we can't SEE the z-order. Instead, we shall ensure
    # that the ticks happen from the TOP LEFT to the bottom right of the grid. Nice and predictable.
    # Note that this stage can also house actors not within the grid, and we must call their ticks too.
    def tick(self) :
    
        self.tickCount += 1
        
        # It is possible for the grid to change during this method (when transitioning from one
        # scene to another), so don't use self.grid later than this, to avoid using the wrong one.
        grid = self.grid
        
        if self.grid :
            
            for ty in range( 0, self.grid.down ) :
                y = grid.down - 1 - ty  # From top downwards

                for x in range( 0, grid.across ) :
                
                    square = grid.squares[x][y]

                    self.tickRole(square.occupant)
                    self.tickRole(square.alternateOccupant)                    

                    # If an invader has nabbed a square, before the actor has finished moving out of the square,
                    # then we must ensure that it gets its tick called by calling it from the square it is entering.
                    if square.entrant and square.entrant.square.occupant != square.entrant :
                        if square.entrant.actor :
                            self.tickRole( square.entrant )
                                

        ZOrderStage.tick(self)

    def tickRole(self, role ) :

        if role :

            if role.latestTick != self.tickCount :
                role.latestTick = self.tickCount

                actor = role.actor
                if actor :
                    if actor.isDead() :
                        self.remove(actor)
                    else :
                        actor.tick();
        
        
    def singleTick(self, actor) :
        role = actor.role
        if not isinstance(role, GridRole ):
            actor.tick();
        else :
            if actor.role.square is None :
                actor.tick();
    

