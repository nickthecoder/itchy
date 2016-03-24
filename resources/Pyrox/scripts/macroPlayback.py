from common import *

game = Itchy.getGame()

# Takes over control of all Player objects, and instead of the input coming from the keyboard,
# it is controlled by parsing a string which holds the keys to be played back.
#
# See MacroRecorder for details about the playback string format.
class MacroPlayback() :

    def __init__(self, playback) :
    
        self.letters = playback
        self.index = -1
        self.currentBitmask = 0
        self.advanceIndex = True
        self.counter = 0
        
        for player in game.findRolesByTag("player") :
            self.interceptPlay( player )
    
    def tick(self) :

        if self.letters is None :
            return

        if self.advanceIndex :
            self.advanceIndex = False
            
            self.counter -= 1
            if self.counter <= 0 :
                
                self.index += 1

                if self.index >= len( self.letters ) :

                    for player in game.findRolesByTag("player") :
                        self.endIntercept( player )
                    self.letters = None
                    
                else :
                    self.counter = 0
                    letter = self.letters[self.index]
                    while letter >= "0" and letter <= "9" :
                        self.counter = self.counter * 10 + int(letter)
                        self.index += 1
                        letter = self.letters[self.index]

                    if self.counter == 0 :
                        self.counter = 1
                    self.currentBitmask = ord(letter) - 64
                    
             
    def interceptPlay(self, player) :

        player.inputLeft = InterceptPlay( self, player.inputLeft, 1 )
        player.inputUp = InterceptPlay( self, player.inputUp, 2 )
        player.inputDown = InterceptPlay( self, player.inputDown, 4 )
        player.inputRight = InterceptPlay( self, player.inputRight, 8 )
   
    def endIntercept(self, player) :
    
        player.inputLeft = player.inputLeft.origInput
        player.inputRight = player.inputRight.origInput
        player.inputUp = player.inputUp.origInput
        player.inputDown = player.inputDown.origInput
      
    def pressed(self, bitmask) :
        self.advanceIndex = True
        return bitmask & self.currentBitmask


class InterceptPlay() :

    def __init__(self, playback, origInput, bitmask) :
        self.playback = playback
        self.origInput = origInput
        self.bitmask = bitmask

    def matches( self, ke ) :
        return self.playback.pressed( self.bitmask )

    def pressed(self) :
        return self.playback.pressed( self.bitmask )


