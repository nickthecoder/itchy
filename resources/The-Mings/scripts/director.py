from common import *

# The size of the pixelations
PIXELATION_SIZE = 6

game = Itchy.getGame()

class Director(AbstractDirector) :

    def onStarted( self ) :
    
        self.inputQuit = Input.find("quit")
            
        guiHeight = 100
        playRect = Rect(0, 0, game.getWidth(), game.getHeight() - guiHeight)
        guiRect = Rect(0, game.getHeight() - guiHeight, game.getWidth(), guiHeight )        

        self.gridStage = ZOrderStage("grid")
        game.getStages().add(self.gridStage)
        self.gridView = StageView( playRect, self.gridStage )
        game.getGameViews().add(self.gridView)
        self.gridStage.setStageConstraint( GridStageConstraint( 10,10 ) )
        self.gridView.enableMouseListener(game)

        self.mainStage = ZOrderStage("main")
        game.getStages().add(self.mainStage)
        self.mainView = StageView(playRect, self.mainStage)
        game.getGameViews().add(self.mainView)
        self.mainView.enableMouseListener(game)

        self.guiStage = ZOrderStage("gui")
        game.getStages().add(self.guiStage)
        self.guiView = StageView(guiRect, self.guiStage)
        game.getGameViews().add(self.guiView)
        self.guiView.enableMouseListener(game)


    def onMessage(self,message) :
        if message == Director.SPRITE_SHEETS_LOADED :
            self.processSpriteSheet()
            
    def onKeyDown(self,kevent) :

        if self.inputQuit.matches(kevent) :
            scene = game.sceneName
            if scene == "menu" :
                game.end()
            else :
                game.startScene( "menu" )

    def scrollTo( self, x, y ) :
        self.mainView.centerOn( x,y )
        self.gridView.centerOn( x,y )

    def processSpriteSheet(self) :
        start_time = time.time()

        self.pixelator = Pixelator( PIXELATION_SIZE, RGBA(255,255,255,128), RGBA(0,0,0,128) )
        self.buttonPixelator = Pixelator( 4, RGBA(255,255,255,128), RGBA(0,0,0,128) )
        
        self.pixelateSprites( "tiny-brick-", "brick-", 1 )

        self.pixelateSprites( "tiny-build-", "builderR-", 15 )
        self.pixelateSprites( "tiny-build-", "builderL-", 15, True )

        self.pixelateSprites( "tiny-walker-", "walkerR-", 10 )
        self.pixelateSprites( "tiny-walker-", "walkerL-", 10, True )

        self.pixelateSprites( "tiny-digger-", "diggerA-", 8, False, -PIXELATION_SIZE/2, 0 )
        self.pixelateSprites( "tiny-digger-", "diggerB-", 8, True , PIXELATION_SIZE/2, 0 )
        
        self.createButton( "tiny-build-09", "buttonBuilder" )
        self.createButton( "tiny-digger-03", "buttonDigger" )
        
        print "Processed images in", time.time() - start_time, "seconds"

    def createButton( self, sourceName, destinationName ) :
    
        source = game.resources.getPose( sourceName )
        if source is None :
            print "Pose", sourceName, "not found"
        else :
            surface = self.buttonPixelator.pixelate( source.surface )
            pose = ImagePose( surface )
            pose.offsetX = surface.width/2
            pose.offsetY = surface.height/2
            poseResource = DynamicPoseResource( game.resources, destinationName, pose )
            
            game.resources.addPose( poseResource )
    
    def pixelateSprites( self, sourcePrefix, destinationPrefix, amount, flip = False, dx = 0, dy = 0 ) :
    
        for i in range(0,amount) :
            sourceName = sourcePrefix + str(i+1).zfill(2)
            destinationName = destinationPrefix + str(i+1).zfill(2)
            pose = game.resources.getPose( sourceName )
            if pose is None :
                print "Pose", sourceName, "not found"
            else :
                newPose = self.pixelator.pixelate( pose, flip, dx, dy )
                newPoseResource = DynamicPoseResource( game.resources, destinationName, newPose )
                game.resources.addPose( newPoseResource )
    

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


