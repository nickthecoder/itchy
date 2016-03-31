package uk.co.nickthecoder.itchy;

/**
 * Holds a 2D position, plus various mathematical functions, such as finding the distance between two points.
 * <p>
 * Primarily used to hold the position of each {@link Actor}.
 * <p>
 * Points are immutable, meaning that once a Point is created, it cannot be changed. For example the method
 * {@link #translate(double, double)} does NOT change this Point, instead it returns a <b>new</b> Point.
 * 
 * @see Actor#getPosition()
 */
public final class Point
{
    private final double x;

    private final double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * A simple getter
     * 
     * @return The x coordinate
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * A simple getter
     * 
     * @return The y coordinate
     */
    public double getY()
    {
        return this.y;
    }

    /**
     * Creates a new Point translated by (x,y)
     * 
     * @param x
     * @param y
     * @return A new Point, which is translated from this by (x,y)
     */
    public Point translate(double x, double y)
    {
        return new Point(this.x + x, this.y + y);
    }

    /**
     * Create a new Point translated a certain distance in the direction give by <code>angleDegrees</code>
     * 
     * @param angleDegrees
     *            The angle in degrees. Zero is along the x axis (East), going anti-clockwise.
     * @param distance
     * @return A new Point, <code>distance</code> away in the direction <code>angleDegrees</code>
     */
    public Point translateDegrees(double angleDegrees, double distance)
    {
        return this.translateRadians(angleDegrees / 180 * Math.PI, distance);
    }

    /**
     * Create a new Point translated a certain distance in the direction give by <code>angleRadians</code>
     * 
     * @param angleRadians
     *            The angle in radians. Zero is along the x axis (East), going anti-clockwise.
     * @param distance
     * @return A new Point, <code>distance</code> away in the direction <code>angleRadians</code>
     */
    public Point translateRadians(double angleRadians, double distance)
    {
        double cosa = Math.cos(angleRadians);
        double sina = Math.sin(angleRadians);

        return this.translate(cosa * distance, (sina * distance));

    }

    /**
     * Similar to {@link #translateDegrees(double, double)}, but as well as moving forwards in the angle given, it also
     * moves sideways (to the left)
     * by a given amount.
     * 
     * @param angleDegrees
     *            The direction of the <code>forwards</code> movement. Zero is along the x axis (East), going
     *            anti-clockwise.
     * @param forwards
     *            The distance to move in the direction <code>angleDegrees</code>
     * @param sideways
     *            The distance to move in the direction <code>angleDegrees + 90</code>
     * @return A new Point
     */
    public Point translateDegrees(double angleDegrees, double forwards, double sideways)
    {
        return translateRadians(angleDegrees / 180 * Math.PI, forwards, sideways);
    }

    /**
     * The same as {@link #translateDegrees(double, double, double)}, but using radians instead of degrees.
     * 
     * @param angleRadians
     *            The direction of the <code>forwards</code> movement. Zero is along the x axis (East), going
     *            anti-clockwise.
     * @param forwards
     *            The distance to move in the direction <code>angleRadians</code>
     * @param sideways
     *            The distance to move in the direction <code>angleDegrees + PI / 2</code>
     * @return A new Point
     */
    public Point translateRadians(double angleRadians, double forwards, double sideways)
    {
        double cosa = Math.cos(angleRadians);
        double sina = Math.sin(angleRadians);

        return this.translate((cosa * forwards) - (sina * sideways), (sina * forwards) + (cosa * sideways));
    }

    /**
     * The distance from this to <code>other</code>
     * 
     * @param other
     * @return
     */
    public double distance(Point other)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * The distance from this to <code>other</code> squared.
     * Many geometric operations need to find the distance between two points, but algorithms can often be optimised by
     * not square rooting the distance early.
     * 
     * @param other
     * 
     * @return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
     */
    public double distanceSquared(Point other)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;

        return dx * dx + dy * dy;
    }

    /**
     * The direction of the other point relative to this point.
     * 
     * @param other
     * @return The angle in degrees
     */
    public double directionDegrees(Point other)
    {
        return directionRadians(other) * 180 / Math.PI;
    }

    /**
     * The direction of the other point relative to this point.
     * 
     * @param other
     * @return The angle in radians
     */
    public double directionRadians(Point other)
    {
        return Math.atan2(other.y - this.y, other.x - this.x);
    }

    /**
     * Move towards <code>other</code> by the given amount.
     * If the distance between <code>this</code> and <code>other</code> <= <code>amount</code>, then do NOT
     * overshoot, and instead return <code>other</code>.
     * <p>
     * If <code>amount</code> is negative, then the movement will be away from <code>other</code>.
     * 
     * @param other
     *            The Point to move towards
     * @param amount
     *            The distance to move
     * @return A new Point, or <code>other</code> if the distance <= <code>amount</code>
     */
    public Point towards(Point other, double amount)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;

        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= amount) {
            return other;
        }
        double scale = amount / distance;

        return new Point(this.x + dx * scale, this.y + dy * scale);
    }

}
