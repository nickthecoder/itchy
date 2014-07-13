import("Grid.js");

Level = Class({
    Extends: SceneDirectorScript,
    
    init: function() {
    },
    
    onActivate: function() {
        var stage = directorScript.gridStage;

        var minX = 1000000;
        var minY = 1000000;
        var maxX = -1000000;
        var maxY = -1000000;

        var i = stage.iterator();
        if ( ! i.hasNext() ) {
            minX = 0;
            minY = 0;
            maxX = 0;
            maxY = 0;
        }
        
        while (i.hasNext()) {
            var actor = i.next();
            var x = actor.getX();
            var y = actor.getY();

            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }
        var squareSize = directorScript.squareSize;
        var across = Math.floor( (maxX - minX) / squareSize) + 1;
        var down = Math.floor( (maxY - minY) / squareSize) + 1;
        
        this.grid = new Grid( squareSize, across, down, minX, minY );
        
        var i = stage.iterator();
        while (i.hasNext()) {
            var actor = i.next();
            if (actor.role.roleScript.placeOnGrid) { // Only GridRole instances (and subclasses) are added to the grid.
                actor.role.roleScript.placeOnGrid( this.grid );
            } else {
                stdout.println( "Skipping non-GridRole object : " + actor.toString() );
            }
        }
    },
    
    tick: function() {
    }
    
    // Other methods include :
    // onMouseDown, onMouseUp, onMouseMove, onKeyDown, onKeyUp, onMessage
});
// TODO Define each editable property like so :
// SceneDirectorScript.addProperty("Level", "exampleInteger", Integer, "Example Integer");
// SceneDirectorScript.addProperty("Level", "exampleDouble", Double, "Example Double");
// SceneDirectorScript.addProperty("Level", "exampleString", String, "Example String");
// SceneDirectorScript.addProperty("Level", "exampleColour", RGBA, "Example Colour");
// NOTE : You must initialise each property to a default value in the 'init' method.

// NOTE. You can access this object from your other scripts using the global variable : 'sceneDirectorScript'.
