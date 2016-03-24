package uk.co.nickthecoder.itchy.collision;

/**
 * All CollisionStrategies that do not just wrap around another CollisionStrategy will need to perform tests for
 * collisions.
 * The test could be pixel based, or based only on bounding rectangle, or some other criteria.
 * This class holds a {@link CollisionTest}, which could be a {@link PixelCollisionTest},
 * {@link BoundingBoxCollisionTest}, or
 * some other, as yet unwritten implementation.
 * 
 * @priority 3
 */
public abstract class AbstractCollisionStrategy implements CollisionStrategy
{
    protected CollisionTest collisionTest;

    public AbstractCollisionStrategy(CollisionTest collisionTest)
    {
        this.collisionTest = collisionTest;
    }

    /**
     * A simple getter.
     * 
     * @return
     * @priority 3
     */
    public CollisionTest getCollisionTest()
    {
        return collisionTest;
    }

    /**
     * A simple setter.
     * 
     * @param value
     * @priority 3
     */
    public void setCollisionTest(CollisionTest value)
    {
        this.collisionTest = value;
    }

}
