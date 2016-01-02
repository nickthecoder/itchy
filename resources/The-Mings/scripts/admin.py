from common import *

from uk.co.nickthecoder.itchy import Pose, PoseResource, ImagePose, AnimationResource
from uk.co.nickthecoder.itchy.animation import Animation, CompoundAnimation, FramedAnimation, Frame

properties = ArrayList()

class Admin(PlainSceneDirector) :

    def onMessage( self, message ) :
        print "Admin: Message", message
        if message == "generateImages" :
            GenerateImages().normal()

        if message == "generateButtons" :
            GenerateImages().buttons()

    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


# Generates images from the sprite sheet.
class GenerateImages :

    def __init__( self ) :
        self.resources = Itchy.getGame().resources
        self.spriteSheet = self.resources.getPose( "theMings" )


    def normal( self ) :
        print "Generating Images from spriteSheet", self.spriteSheet
        self.scale = 6

        #self.generateAnimation( 6,0,   8,  6,10, "walkL", True, 4 )
        #self.generateAnimation( 6,0,   8,  6,10, "walkR", False, 4 )

        #self.generateAnimation( 5,2*20,  4,  6,10, "fall", False, 4 )
        #self.generateAnimation( 2,11*20, 16,  14,10, "squish", False, 4, False )

        #self.generateAnimation( 4,3*20, 16,  10,10, "blocker", False, 4 )

        self.generateAnimation( 0, 4*20+2, 8,  9,10, "climberR", False, 4 )
        self.generateAnimation( 0, 4*20+2, 8,  9,10, "climberL", True, 4 )

        self.generateAnimation( 4, 6*20, 16,  12,10, "smasherR", False, 4 )
        self.generateAnimation( 4, 6*20, 16,  12,10, "smasherL", True, 4 )

        print "Saving Resources"
        self.resources.save()
        print "Saved Resources"


    def buttons( self ) :

        print "Generating Buttons from spriteSheet", self.spriteSheet
        self.scale = 4

        self.pixelate( 4,3*20, 10,10, self.scale/2, self.scale/2, "buttonBlocker", False )
        self.pixelate( 0,4*20+2, 10,10, self.scale/2, self.scale/2, "buttonClimber", False )
        self.pixelate( 3,6*20, 10,10, self.scale/2, self.scale/2, "buttonSmasher", False )

        print "Saving Resources"
        self.resources.save()
        print "Saved Resources"

    def generateAnimation( self, x, y, frames, width, height, name, flip, delay, loop=True ) :

        print "Generating animation :", name
        animation = CompoundAnimation( True )
        animation.loops = 0 if loop else 1
        framed = FramedAnimation()
        animation.add( framed )

        for i in range( 0, frames ) :
            frameName = name + str(i)
            pr = self.pixelate( x+i*20,y, width,height, width/2,height, frameName, flip )
            frame = Frame( frameName, pr.pose )
            frame.setDelay( delay )
            framed.addFrame( frame )

        ar = self.resources.getAnimationResource( name )
        if ar is None :
            self.resources.addAnimation( AnimationResource(self.resources, name, animation ) )
        else :
            ar.animation = animation

        return animation

    # x,y : The position of the image within the sprite sheet
    # dx,dy : The size of the (unscaled) image 
    # ox,oy : The offset X,Y (See Pose)
    # name : The name of the pose, which will also be the basis for the filename
    # flippedXName : If set, then ANOTHER pose is created flipped left/right.
    def pixelate( self, x, y, dx, dy, ox, oy, name, flip ) :

        src = self.spriteSheet.getSurface()
        cut = Surface( dx, dy, True )
        src.blit( Rect(x,y,dx,dy), cut, 0, 0, Surface.BlendMode.COMPOSITE )

        if flip :
            cut = cut.zoom(-1,1,False)

        zoomed = cut.rotoZoom( 0, self.scale, False )


        for i in range(0,dx) :
            for j in range(0,dy) :
                color = cut.getPixelColor( i, j )
                borders = ""
                if self.changedColor( cut, color, i, j-1 ) :
                    borders += "N"
                if self.changedColor( cut, color, i, j+1 ) :
                    borders += "S"
                if self.changedColor( cut, color, i+1, j ) :
                    borders += "E"
                if self.changedColor( cut, color, i-1, j ) :
                    borders += "W"
                if borders != "" :
                    ninePatch = self.resources.getNinePatch( "border" + borders )
                    border = ninePatch.createSurface( self.scale, self.scale )
                    border.blit( zoomed, i * self.scale, j * self.scale )

        return self.save( zoomed, name, ox, oy )

    def save( self, surface, name, ox, oy ) :
        relativeFilename = "images/" + name + ".png"
        filename = self.resources.resolveFilename( relativeFilename )

        surface.saveAsPNG( filename )
        pr = PoseResource( self.resources, name, relativeFilename )
        pr.pose.setOffsetX( self.scale * ox )
        pr.pose.setOffsetY( self.scale * oy )
        self.resources.addPose( pr )            
        return pr

    def changedColor( self, surface, color, x, y ) :
        if x < 0 or y < 0 :
            return True
        if y >= surface.getHeight() or x >= surface.getWidth() :
            return True
        return color != surface.getPixelColor( x, y )
    

