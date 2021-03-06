from common import *  #@UnusedWildImport

properties = ArrayList()

game = Itchy.getGame()
director = game.getDirector()

class Menu(PlainSceneDirector) :

    def onActivate(self) :
        game = Itchy.getGame()
        director = game.getDirector()
        director.showFancyMouse()

        self.inputStart = Input.find("start")
        self.inputMenu = Input.find("menu")
        self.inputAbout = Input.find("about")
        self.inputEditor = Input.find("editor")
    
    
    def onKeyDown(self,event) :
    
        if self.inputStart.matches(event) :
            game.startScene("easy-1")
            
        elif self.inputMenu.matches(event) :
            if game.sceneName != "menu" :
                game.startScene("menu")
                        
        elif self.inputAbout.matches(event) :
            game.startScene("about")
    
        elif self.inputEditor.matches(event) :
            game.startEditor()
    
    
    # The menu has flying rocks, but we don't care about them, so do nothing.
    def addRocks(self, diff) :
        pass


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName( SceneDirector, self.__module__ + ".py" )


