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


    def onMessage(self,message) :
        if message == Director.SPRITE_SHEETS_LOADED :
            self.processSpriteSheet()
        if message == Director.POSES_LOADED :
            self.processPoses()
            
    def onKeyDown(self,kevent) :

        if self.inputQuit.matches(kevent) :
            scene = game.sceneName
            if scene == "menu" :
                game.end()
            else :
                game.startScene( "menu" )

    def scrollTo( self, x, y ) :
        game.layout.findView("main").centerOn( x,y )
        game.layout.findView("grid").centerOn( x,y )

    def processSpriteSheet(self) :
        start_time = time.time()

        self.pixelator = Pixelator( PIXELATION_SIZE, RGBA(255,255,255,128), RGBA(0,0,0,128) )
        self.buttonPixelator = Pixelator( 4, RGBA(255,255,255,128), RGBA(0,0,0,128) )
        
        self.pixelateSprites( "tiny-blocker-", "blocker-", 16 )

        self.pixelateSprites( "tiny-bomber-", "bomber-", 9 )

        self.pixelateSprites( "tiny-builder-", "builderR-", 16 )
        self.pixelateSprites( "tiny-builder-", "builderL-", 16, True )

        self.pixelateSprites( "tiny-digger-", "diggerA-", 8, False, -PIXELATION_SIZE/2, 0 )
        self.pixelateSprites( "tiny-digger-", "diggerB-", 8, True , PIXELATION_SIZE/2, 0 )

        self.pixelateSprites( "tiny-explode-", "explode-", 14 )
        
        self.pixelateSprites( "tiny-faller-", "fallerA-", 4 )
        self.pixelateSprites( "tiny-faller-", "fallerB-", 4, True )

        self.pixelateSprites( "tiny-floaterA-", "floaterA-", 4 )
        self.pixelateSprites( "tiny-floaterB-", "floaterB-", 4 )
        self.pixelateSprites( "tiny-floaterB-", "floaterC-", 4, True )
        
        self.pixelateSprites( "tiny-miner-", "minerR-", 24 )
        self.pixelateSprites( "tiny-miner-", "minerL-", 24, True )

        self.pixelateSprites( "tiny-splat-", "splat-", 16 )
        
        self.pixelateSprites( "tiny-smasher-", "smasherR-", 32 )
        self.pixelateSprites( "tiny-smasher-", "smasherL-", 32, True )

        self.pixelateSprites( "tiny-walker-", "walkerR-", 8 )
        self.pixelateSprites( "tiny-walker-", "walkerL-", 8, True )


        self.createButton( "tiny-buttonBlocker", "buttonBlocker" )
        self.createButton( "tiny-buttonBuilder", "buttonBuilder" )
        self.createButton( "tiny-buttonClimber", "buttonClimber" )
        self.createButton( "tiny-buttonDigger" , "buttonDigger"  )
        self.createButton( "tiny-buttonSmasher", "buttonSmasher" )
        self.createButton( "tiny-buttonFloater", "buttonFloater" )
        
        self.pixelateSprite( "tiny-brick", "brick" )
        self.pixelateSprite( "tiny-highlight", "highlight" )
        self.pixelateSprite( "tiny-lookDown", "lookDown" )
        self.pixelateSprite( "tiny-lookLeft", "lookLeft" )
        self.pixelateSprite( "tiny-lookRight", "lookRight" )

        print "Processed images in", time.time() - start_time, "seconds"


    def processPoses(self) :
        self.convertToMask( "crater", "craterMask" )
        self.convertToMask( "smashL", "smashLMask" )
        self.convertToMask( "smashR", "smashRMask" )
        self.convertToMask( "dig", "digMask" )


    def convertToMask( self, poseName, newName ) :
        # Convert the image, so that it is suitable as a mask.
        srcPose = game.resources.getPose( poseName )
        if srcPose is None :
            print "Pose", poseName, "not found."
            return

        srcSurface = srcPose.surface
        newSurface = Surface( srcSurface.width, srcSurface.height, True )
        newSurface.fill( RGBA( 255,255,255,255 ) )
        srcSurface.blit( newSurface, Surface.BlendMode.RGBA_SUB )
        newPose = ImagePose( newSurface, srcPose.offsetX, srcPose.offsetY )

        game.resources.addPose( DynamicPoseResource( newName, newPose) )
        
    def createButton( self, sourceName, destinationName ) :
    
        source = game.resources.getPose( sourceName )
        if source is None :
            print "Pose", sourceName, "not found"
        else :
            surface = self.buttonPixelator.pixelate( source.surface )
            pose = ImagePose( surface )
            pose.offsetX = surface.width/2
            pose.offsetY = surface.height/2
            poseResource = DynamicPoseResource( destinationName, pose )
            
            game.resources.addPose( poseResource )
    
    def pixelateSprites( self, sourcePrefix, destinationPrefix, amount, flip = False, dx = 0, dy = 0 ) :
    
        for i in range(0,amount) :
            sourceName = sourcePrefix + str(i+1).zfill(2)
            destinationName = destinationPrefix + str(i+1).zfill(2)
            self.pixelateSprite( sourceName, destinationName, flip, dx, dy )
            
    def pixelateSprite( self, sourceName, destinationName, flip = False, dx = 0, dy = 0 ) :

        pose = game.resources.getPose( sourceName )
        if pose is None :
            print "Pose", sourceName, "not found"
        else :
            newPose = self.pixelator.pixelate( pose, flip, dx, dy )
            newPoseResource = DynamicPoseResource( destinationName, newPose )
            game.resources.addPose( newPoseResource )


    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( Director, self.__module__ + ".py" )


