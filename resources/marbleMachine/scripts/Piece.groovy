import uk.co.nickthecoder.itchy.*
import uk.co.nickthecoder.itchy.property.*
import uk.co.nickthecoder.itchy.role.*
import uk.co.nickthecoder.itchy.util.*

abstract class Piece extends AbstractRole
{
    protected edges = []

    protected rotating = false
        
    protected rotationSpeed = 0
        
    void onBirth()
    {
        addTag("piece")
        if ( costumeFeatures.mirror ) {
            for ( int i = 0; i < edges.size(); i ++ ) {
                edges[i][0] = -edges[i][0]
                edges[i][2] = -edges[i][2]
                edges[i][4] = -edges[i][4]
            }
        }
    }

    boolean checkCollision( ball ) {
        for ( Double[] edge : edges ) {
            if (checkCollision( edge, ball, true )) {
                return true
            }
        }
        return false
    }
    
    void tick() {
    
        if ( rotating || actor.appearance.direction != 0 ) {
            // Need to rotate
            def returning = ! collided( "ball" )
            def rotateBy = rotationSpeed == 0 ? 3 : rotationSpeed
            if ( costumeFeatures.mirror ) {
                rotateBy = -rotateBy
            }
            if ( returning ) {
                rotateBy = -rotateBy
                rotateBy += 2
                rotating = false
            }
            def newDirection =  actor.appearance.direction + rotateBy
            
            if ( returning ) {
                if ( ( newDirection >= 0 && rotateBy > 0 ) || ( newDirection <= 0 && rotateBy < 0 ) ) {
                    newDirection = 0
                }
            } else {            
                if ( newDirection < -90 ) {
                    newDirection = -90
                    rotating = false
                    return
                }
                if ( newDirection > 90 ) {
                    newDirection = 90
                    rotating = false
                    return
                }
            }
            actor.appearance.direction = newDirection
        }
    }
    
    boolean checkCollision( edge, ball, performCollision ) {
        def direction = actor.appearance.directionRadians
        def startX = Math.cos( direction ) * edge[0] - Math.sin( direction ) * edge[1]
        def startY = Math.cos( direction ) * edge[1] + Math.sin( direction ) * edge[0]
        def endX = Math.cos( direction ) * edge[2] - Math.sin( direction ) * edge[3]
        def endY = Math.cos( direction ) * edge[3] + Math.sin( direction ) * edge[2]
    
        def ballX = ball.actor.x - actor.x 
        def ballY = ball.actor.y - actor.y
        
        // Vector of ball from start of line
        def bx = ballX - startX
        def by = ballY - startY

        // Vector of the line
        def lx = endX - startX
        def ly = endY - startY
        
        //println( "l = ${lx},${ly}" )
        //println( "b = ${bx},${by}" )

        // Length of the line
        def lineLength = Math.sqrt( lx * lx + ly * ly )
        def bMag = Math.sqrt( bx*bx + by*by )
        
        def along = dotProduct( bx, by, lx, ly ) / lineLength / lineLength
        //println( "Along ${along}" )
        if ( along < 0 || along > 1 ) {
            // Not within the line segment
            //println( "Not within line segment" )
            return false
        }
        
        // Collision point along the line
        def ix = startX + along * lx
        def iy = startY + along * ly
        
        //println( "Collision point ${ix}, ${iy}" )
        // Vector normal to the line
        def nx = ballX-ix
        def ny = ballY-iy
        
        // Distance of the center of the ball to the line
        def dist = Math.sqrt( nx*nx + ny*ny )
        if ( dist > ball.radius ) {
            //println( "Too far away" )
            return false
        }
        
        // There is a collision
        
        def turn = edge[4]
        
        // 2 for perfectly bouncy, 1 for no bounce, 0 to pass right through.
        
        def coRest = 1.2
        
        def bang = dotProduct( ball.vx, ball.vy, nx, ny ) / dist * coRest
        def bangX = bang * nx / dist
        def bangY = bang * ny / dist
        
        if ( bang < 0 ) {
            //println ("Bang! ${ball.vx},${ball.vy} : ${bang} -> ${bangX},${bangY}")
            if (performCollision) {
                if (turn != 0) {
                    rotationSpeed = -bang * 3.6
                    if (rotationSpeed > 20) {
                        rotationSpeed = 20
                    }
                    rotating = true
                } else {
                    ball.vx -= bangX
                    ball.vy -= bangY
                }
            }
        }
        
        return true
    }
    
    public static double dotProduct( x1, y1, x2, y2 ) {
        return x1* x2 + y1 * y2
    }
    
    
    public CostumeFeatures createCostumeFeatures(Costume costume)
    {
        return new PieceFeatures()
    }
}


public class PieceFeatures implements CostumeFeatures
{
    protected static properties = new ArrayList()
    
    static {
        properties.add( new BooleanProperty( "mirror" ) )
    }
    
    def mirror = false

    // Boiler plate code - no need to change this
    public ArrayList getProperties()
    {
        return properties
    }

}

