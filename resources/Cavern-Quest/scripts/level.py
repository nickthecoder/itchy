from common import * #@UnusedWildImport

from grid import Grid
from gridRole import GridRole

properties = ArrayList()
properties.add( IntegerProperty( "blasts" ) )
properties.add( IntegerProperty( "respawnChance" ).min(1).hint( "One in ? (1=Always)") )

game = Itchy.getGame()

# The SceneDirector used by all of the play levels.
#
# After a scene has loaded, places all GridRole objects into a Grid. Without this grid,
# objects wouldn't be able to look around them, and react with each other.
#
class Level(PlainSceneDirector) :

    def __init__(self) :
                    
        self.blasts = 100
        self.player = None
        self.respawnChance = 4
        
        self.random = Random()

    def loading( self, scene ) :
        print "loading"

        print "Calculating grid size"
        # Calculate the size of the grid needed to fit all of the actors
        stage = game.layout.findStage("grid")

        minX = 1000000
        minY = 1000000
        maxX = -1000000
        maxY = -1000000

        i = stage.iterator()
        if ( not i.hasNext() ) :
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
                
        squareSize = game.director.squareSize
        across = math.floor( (maxX - minX) / squareSize) + 1
        down = math.floor( (maxY - minY) / squareSize) + 1
        
        print "Creating grid"
        self.grid = Grid( squareSize, across, down, minX, minY )
        game.layout.findStage("grid").grid = self.grid

        # Load the glass stage on top of the current scene.
        print "Loading the glass"
        game.mergeScene("glass")


    def onActivate( self ) :
    
        print "onActivate"
        
        for player in game.findRoleByTag("player") :
            self.player = player

        print "Looking for spawn sites"
        self.spawnLocations = []
        for spawn in game.findRoleByTag("spawn") :
            self.spawnLocations.append((spawn.actor.x, spawn.actor.y))
            spawn.deathEvent("remove")
            
        if self.player :
            game.layout.findView("grid").centerOn(self.player.actor)
            game.layout.findView("plain").centerOn(self.player.actor)


    def respawn( self, nasty ) :

        if len(self.spawnLocations) == 0 :
            return
            
        if self.respawnChance > 1 :
            if self.random.nextInt( self.respawnChance ) > 0 :
                return

        # Try 10 times to find an empty spawn location, and if none is found, then give up.            
        for i in range(0,10) :
            index = self.random.nextInt( len(self.spawnLocations) )
            x = self.spawnLocations[index][0]
            y = self.spawnLocations[index][1]
            square = self.grid.getSquareByPixel(x, y)
            if square.getOccupant() == self.grid.empty :
                newNastyActor = nasty.actor.costume.createActor("default")
                newNastyActor.moveTo(x,y)
                Itchy.getGame().layout.findStage( "grid" ).add( newNastyActor )
                return
     
    def tick(self) :

        if self.player :
            self.player.playerTick()
                
        PlainSceneDirector.tick(self)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )


