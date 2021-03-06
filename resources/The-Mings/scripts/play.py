from common import *

from uk.co.nickthecoder.itchy.collision import SinglePointCollisionStrategy

from highlight import Highlight

properties = ArrayList()
properties.add( IntegerProperty( "limitsLeft" ) )
properties.add( IntegerProperty( "limitsRight" ) )
properties.add( IntegerProperty( "limitsTop" ) )
properties.add( IntegerProperty( "limitsBottom" ) )

game = Itchy.getGame()
director = game.getDirector()

class Play(PlainSceneDirector) :

    def __init__(self) :
        self.limitsLeft = 0
        self.limitsRight = 1000
        self.limitsBottom = 0
        self.limitsTop = 800

        self.neighbourhood = StandardNeighbourhood( 80 )

        self.scrollSpeed = 4
        self.scrollX = 0
        self.scrollY = 0

        self.inputScrollLeft = Input.find("scrollLeft")
        self.inputScrollRight = Input.find("scrollRight")
        self.inputScrollUp = Input.find("scrollUp")
        self.inputScrollDown = Input.find("scrollDown")

        self.jobButton = None # The currently selected JobButton
        self.highlight = None # a Highlight object or None if no Ming is highlighted


    def loading( self, scene ) :
        print "Loading gui scene"
        game.mergeScene("gui")

    def onActivate(self) :
        width = game.getWidth()
        height = game.getHeight()

        # Adjust the limits, so that they reference the CENTER of the screen
        # MORE Change to the VISIBLE area i.e. the size of the main view.
        self.limitsLeft += width/2
        self.limitsRight -= width/2
        self.limitsTop -= height/2
        self.limitsBottom += height/2
        

    def tick(self) :

        if self.inputScrollLeft.pressed() :
            self.scrollBy( -self.scrollSpeed, 0 )
        elif self.inputScrollRight.pressed() :            
            self.scrollBy( self.scrollSpeed, 0 )
        elif self.inputScrollUp.pressed() :            
            self.scrollBy( 0, self.scrollSpeed )
        elif self.inputScrollDown.pressed() :            
            self.scrollBy( 0, -self.scrollSpeed )

        director.scrollTo( self.scrollX, self.scrollY )


    def scrollTo( self, x, y ) :
        self.scrollX = x
        self.scrollY = y
        self.__scrollLimit()


    def scrollBy( self, dx, dy ) :
        self.scrollX += dx
        self.scrollY += dy
        self.__scrollLimit()


    def __scrollLimit( self ) :

        if self.scrollX < self.limitsLeft :
            self.scrollX = self.limitsLeft

        if self.scrollX > self.limitsRight :
            self.scrollX = self.limitsRight

        if self.scrollY < self.limitsBottom :
            self.scrollY = self.limitsBottom

        if self.scrollY > self.limitsTop :
            self.scrollY = self.limitsTop


    def pickJob( self, jobButton ) :
        if self.jobButton is not None :
            # MORE Unhighlight the old button
            pass

        self.jobButton = jobButton
        # MORE Highlight the button
        
        if self.highlight is not None :
            self.highlight.parent.role.assignJob( jobButton.job )
            self.selectMing(None)


    def selectMing( self, ming ) :
        if self.highlight is not None :
            self.highlight.getActor().kill()
        if ming is None :
            self.highlight = None
        else :
            self.highlight = Highlight( ming.getActor() )
            self.highlight.createActor().event( "highlight" )


    def onMouseDown( self, event ) :
        mainView = game.layout.findView("main")
        mainView.adjustMouse( event )
        if event.button == MouseButtonEvent.BUTTON_LEFT :
            ming = game.findNearestRole( "ming",event.x ,event.y-20 ) #Nearest to their middle, not their feet.
            if ming is not None and ming.getActor().hitting( event.x, event.y ) :
                self.selectMing( ming )
            else :
                self.selectMing( None )

        mainView.unadjustMouse( event )

    def chooseCollisionStrategy( self, actor ) :
        #return SinglePointCollisionStrategy( actor, self.neighbourhood )
        return NeighbourhoodCollisionStrategy( actor, self.neighbourhood )



    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( CostumeProperties, self.__module__ + ".py" )


