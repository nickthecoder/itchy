from common import *

game = Itchy.getGame()

# Records the up/down/left/right keystrokes that Player objects test for.
# Saves the results in a string, which consists of a number followed by a bitmask of the keys
# that were pressed in the frame. left=1, up=2, down=4, right=8. These are "OR"ed together,
# and then saved as chr(n+64), so left is "A", up is "B", left and up together is "C" etc.
# If the player does not ask for any keypresses in a frame, then that frame is ignored.
# However, if it does ask, but no key was pressed then "@" is used.
# To keep the string short, mulitple keypresses are prefixed by the number of times they were pressed.
# For example, "2A@4C" means "left,left,no keys pressed,up,up,up,up".
#
# Used by class Level, in conjunction with MacroPlayback, which can parse these strings, and immitate
# the input. This allows games to be recorded and played back, which is useful for long complex levels.
class MacroRecorder() :

    def __init__(self) :
        self.recorded = ""
        # In order to compress repeated keys, remember the last key, and the number of times it was pressed
        self.previousLetter = None
        self.previousCount = 0
        
        self.bitmask = 0
        self.asked = False
        
    def startRecording(self) :
        for player in game.findRolesByTag("player") :
            self.interceptRecord( player )

    def interceptRecord(self, player) :
    
        player.inputLeft = InterceptRecord( self, player.inputLeft, 1 )
        player.inputUp = InterceptRecord( self, player.inputUp, 2 )
        player.inputDown = InterceptRecord( self, player.inputDown, 4 )
        player.inputRight = InterceptRecord( self, player.inputRight, 8 )

    def endIntercept(self, player) :
    
        player.inputLeft = player.inputLeft.origInput
        player.inputRight = player.inputRight.origInput
        player.inputUp = player.inputUp.origInput
        player.inputDown = player.inputDown.origInput

    def record(self, bitmask) :
        self.bitmask = self.bitmask | bitmask
        self.asked = True

    def noMatch(self) :
        self.asked = True
    
    
    def tick(self) :
        if not self.asked :
            return

        letter = chr(64 + self.bitmask)
        # I'm operating a one letter delay, so that if keys are repeated, rather than repeating the same key,
        # it just puts the count followed by the letter. e.g. 4l2u is the same as lllluu
        if self.previousLetter == letter :
            self.previousCount += 1                
        else :
            if self.previousLetter is not None :
                if self.previousCount == 1 :
                    self.recorded += self.previousLetter
                else :
                    self.recorded += str(self.previousCount) + self.previousLetter

            self.previousCount = 1
            self.previousLetter = letter

        self.bitmask = 0
        self.asked = False
        
    def endRecording(self) :
        for player in game.findRolesByTag("player") :
            self.endIntercept( player )

    def getRecording(self) :
        self.noMatch()
        self.tick()

        return self.recorded

class InterceptRecord() :

    def __init__(self, recorder, origInput, bitmask) :
        self.recorder = recorder
        self.origInput = origInput
        self.bitmask = bitmask
        
    def matches( self, ke ) :
        if self.origInput.matches(ke) :
            self.recorder.record( self.bitmask )
            return True
        else :
            self.recorder.noMatch()
        return False

    def pressed(self) :
        if self.origInput.pressed() :
            self.recorder.record( self.bitmask )
            return True
        else :
            self.recorder.noMatch()
        return False

