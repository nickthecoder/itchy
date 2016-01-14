from common import *

from gridRole import GridRoleCostumeProperties

costumeProperties = ArrayList()
costumeProperties.add( BooleanProperty( "roundedNE" ).label( "Rounded NE" ) )
costumeProperties.add( BooleanProperty( "roundedSE" ).label( "Rounded SE" ) )
costumeProperties.add( BooleanProperty( "roundedSW" ).label( "Rounded SW" ) )
costumeProperties.add( BooleanProperty( "roundedNW" ).label( "Rounded NW" ) )

class RoundProperties(GridRoleCostumeProperties) :

    def __init__(self,costume) :
        super(RoundProperties,self).__init__(costume)

        self.roundedNE = False
        self.roundedSE = False
        self.roundedSW = False
        self.roundedNW = False

    def update(self, role) :

        role.tag("roundedNE", self.roundedNE)
        role.tag("roundedSE", self.roundedSE)
        role.tag("roundedSW", self.roundedSW)
        role.tag("roundedNW", self.roundedNW)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties


