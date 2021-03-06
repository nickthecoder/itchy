from common import * #@UnusedWildImport

from moving import Moving

properties = ArrayList()


costumeProperties = ArrayList()

costumeProperties.add(DoubleProperty("impulse").hint("recoils the ship"))
costumeProperties.add(IntegerProperty("strength"))
costumeProperties.add(DoubleProperty("firePeriod"))
costumeProperties.add(DoubleProperty("explosiveness").hint("The momentum added to the fragments"))

class Bullet(Moving) :

    def __init__(self) :
        Moving.__init__(self)
        self.speed = 1

    def tick(self) :
        Moving.tick(self)
        if self.actor.appearance.alpha < 20 :
            self.actor.kill()
            return
        
        for role in self.collisions("shootable") :
            if not role.getActor().isDying() :
                role.shot(self)
                self.getActor().kill()
                return

        
    def onMessage(self, message) :
        if message == "die" :
            # Sent at the end of the fade out animation. Its the animation that determines the bullets max life span.
            self.getActor().kill()


    def createCostumeFeatures(self, costume) :
        return BulletFeatures(costume)


    # Boiler plate code - no need to change this
    def getProperties(self):
        return properties

    # Boiler plate code - no need to change this
    def getClassName(self):
        return ClassName(Role, self.__module__ + ".py")


class BulletFeatures(CostumeFeatures) :

    def __init__(self, costume) :
        super(BulletFeatures, self).__init__()
        self.impulse = 1
        self.strength = 1
        self.firePeriod = 1
        self.explosiveness = 1

    # Boiler plate code - no need to change this
    def getProperties(self):
        return costumeProperties

