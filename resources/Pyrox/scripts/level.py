from uk.co.nickthecoder.itchy import Itchy
from uk.co.nickthecoder.itchy import PlainSceneDirector
from uk.co.nickthecoder.itchy import AbstractRole
from uk.co.nickthecoder.itchy import Input
from uk.co.nickthecoder.itchy.util import ClassName


import math

from java.util import ArrayList

from gridRole import GridRole
from grid import Grid

properties = ArrayList()

class Level(PlainSceneDirector) :

    def __init__(self) :
    
        self.inputToggleInfo = Input.find("toggleInfo")
        
        self.collectablesRemaining = 0
        self.showInfo = False

    def onActivate( self ) :
        
        # Load the glass stage on top of the current scene.
        Itchy.getGame().loadScene("glass", True)
        
        self.droppedFramesRole = Itchy.getGame().findRoleById("droppedFrames")
        
        # Calculate the size of the grid needed to fit all of the actors
        stage = Itchy.getGame().getDirector().gridStage

        minX = 1000000
        minY = 1000000
        maxX = -1000000
        maxY = -1000000

        i = stage.iterator()
        if ( ! i.hasNext() ) :
            minX = 0
            minY = 0
            maxX = 0
            maxY = 0
        
        while (i.hasNext()) :
            actor = i.next()
            x = actor.getX()
            y = actor.getY()

            if x < minX :
                minX = x

            if x > maxX :
                maxX = x

            if y < minY :
                minY = y

            if y > maxY :
                maxY = y

        squareSize = Itchy.getGame().getDirector().squareSize
        across = math.floor( (maxX - minX) / squareSize) + 1
        down = math.floor( (maxY - minY) / squareSize) + 1
        
        self.grid = Grid( squareSize, across, down, minX, minY )
        
        # Add all of the GridRoles to the grid
        i = stage.iterator()
        while (i.hasNext()) :
            actor = i.next()
            role = actor.getRole()
            if isinstance( role, GridRole ) :
                if not role.getActor().isDead() :
                    role.placeOnGrid( self.grid )
            else :
                print "Skipped non-GridRole object :", role


    def onKeyDown(self, kevent) :
    
        if self.inputToggleInfo.matches(kevent) :
            self.toggleInfo()
        

    def toggleInfo( self ) :

        self.showInfo = not self.showInfo
        alpha = 255 if self.showInfo else 0
        
        if self.droppedFramesRole :
            self.droppedFramesRole.getActor().getAppearance().setAlpha( alpha )
        
    # When the number of collectables remaining is zero, tell all gates to open.
    def collected( self, amount ) :
        self.collectablesRemaining -= amount
        if self.collectablesRemaining <= 0 :

            for gate in AbstractRole.allByTag( "gate" ) :
                gate.onMessage("open")

    # TODO Other methods include :
    # onActivate, onDeactivate(), onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
    
    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


