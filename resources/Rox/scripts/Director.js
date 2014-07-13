Director = Class({

    Extends: DirectorScript,

    init: function() {
        // Super();
        this.squareSize = 50;
    },
    
    onStarted: function() {
      var screenRect = new jame.Rect(0, 0, game.getWidth(), game.getHeight());

      this.plainStage = new itchy.ZOrderStage("plain");
      game.getStages().add(this.plainStage);
      this.plainView = new itchy.StageView(screenRect, this.plainStage);
      game.getGameViews().add(this.plainView);
      this.plainView.enableMouseListener(game);

      this.gridStage = new itchy.ZOrderStage("grid");
      game.getStages().add(this.gridStage);
      this.gridView = new itchy.StageView(screenRect, this.gridStage);
      game.getGameViews().add(this.gridView);

      this.gridStage.setStageConstraint( new itchy.GridStageConstraint( this.squareSize, this.squareSize ) );      
    },
    
    tick: function() {
        // TODO Called 50 times a second.
        // Consider using a SceneDirector, rather than putting code here.
    }
    
    // Other methods include :
    // onActivate, onDeactivate, onQuit, onKeyDown, onKeyUp, onMouseDown, onMouseUp, onMouseMove, onMessage
    // Note : SceneDirector is often a better place to handle mouse and key events.
});

// NOTE. You can access this object from your other scripts using the global variable : 'directorScript'.

