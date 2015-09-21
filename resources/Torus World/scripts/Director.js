ZOrderStage = itchy.ZOrderStage;
WrappedCollisionStrategy = itchy.collision.WrappedCollisionStrategy;
WrappedStageView = itchy.WrappedStageView;
StageView = itchy.StageView;

Director = Class({

    Extends : DirectorScript,

    init : function() {
        this.score = 0;
        this.lives = 0;
    },

    onStarted : function() {
        // Don't create default stages and views, because we want to use a
        // special WrappedStageView
        stdout.println("Creating custom stages and views. " + game);

        var screenRect = Rect(0, 0, game.getWidth(), game.getHeight());

        this.mainStage = ZOrderStage("main");
        game.getStages().add(this.mainStage);

        // We need a separate view for the "lives" in the top left, because we
        // don't want those to
        // wrap round to the bottom of the screen.
        this.hudStage = ZOrderStage("hud");
        game.getStages().add(this.hudStage);

        this.mainView = WrappedStageView(screenRect, this.mainStage);
        this.mainView.wrap(game.getHeight(), game.getWidth(), 0, 0);
        game.getGameViews().add(this.mainView);

        this.hudView = StageView(screenRect, this.hudStage);
        game.getGameViews().add(this.hudView);

        this.hudView.enableMouseListener(game);
        this.collisionStrategy = WrappedCollisionStrategy(this.mainView);
    },

    startScene : function(sceneName) {
        stdout.println("Starting scene : " + sceneName);
        if (game.pause.isPaused()) {
            game.pause.unpause();
        }
        if (sceneName == "menu" && game.getSceneName() == "menu") {
            return true;
        }
        var transition = itchy.extras.SceneTransition.fade();
        if (game.getSceneName() == "menu") {
            transition = itchy.extras.SceneTransition.slideRight();
        }
        if (sceneName == "menu") {
            transition = itchy.extras.SceneTransition.slideLeft();
        }
        return new itchy.extras.SceneTransition(transition).transition(sceneName);
    },

    onMessage : function(message) {
        if (message == "start") {
            this.startGame("1");
        }

        if (message == "continue") {
            this.startGame(game.getSceneName());
        }
    },

    startGame : function(sceneName) {
        if (!sceneName) {
            sceneName = "1";
        }
        this.score = 0;
        this.lives = 3;
        this.startScene(sceneName);
    },

    addPoints : function(points) {
        this.score += points;
    },

    onWindowEvent : function(event) {
        if (event.lostMouseFocus()) {
            game.mouse.showRegularMousePointer(true);
            return true;
        } else if (event.gainedMouseFocus()) {
            this.showFancyMouse();
            return true;
        }
        return false;
    },

    showFancyMouse : function() {
        var mousePointer = new itchy.extras.SimpleMousePointer("mouse");
        game.mouse.setMousePointer(mousePointer);
        new itchy.role.Explosion(mousePointer.getActor())
            .dependent().forever().follow().offset(40, -33)
            .projectilesPerTick(1).spread(-20,-80).distance(10).randomSpread()
            .speed(1, 2, 0, 0).fade(3).eventName("spark").createActor();
        new itchy.role.OnionSkin(mousePointer.getActor()).alpha(128).fade(3).every(1).createActor();
    }

});
