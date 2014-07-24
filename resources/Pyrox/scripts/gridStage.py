from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import ZOrderStage

from square import Square
from gridRole import GridRole

class GridStage(ZOrderStage) :

    def __init__(self, name) :
        ZOrderStage.__init__(self, name)
        self.grid = None

    # In this strategy game, we want PREDICTABLE behaviour, and the standard ZOrderStage doesn't give us that.
    # In ZOrderStage, the ticks are done in z-order, but we can't SEE the z-order. Instead, we shall ensure
    # that the ticks happen from the top left to the bottom right of the grid. Nice and predictable.
    # Note that this stage can also house actors not within the grid, and we must call their ticks too.
    def tick(self) :
    
        if self.grid :
            for row in self.grid.squares :
                for square in row :
                    role = square.occupant
                    if role :
                        actor = role.getActor()
                        if role.movedForward :
                            role.movedForward = False
                        else :
                            if actor.isDead() :
                                self.remove(actor)
                            else :
                                actor.tick();

                    # If an invader has nabbed a square, before the actor has finished moving out of the square,
                    # then we must ensure that it gets it tick called by calling it from the square it is entering.
                    if square.entrant and square.entrant.square.occupant != square.entrant :
                        square.entrant.getActor().tick();


        ZOrderStage.tick(self)
            
    def singleTick(self, actor) :
        role = actor.role
        if not isinstance(role, GridRole ):
            actor.tick();
        else :
            if actor.role.square is None :
                actor.tick();
    

