package uk.co.nickthecoder.itchy.util;

import java.util.Random;

import uk.co.nickthecoder.itchy.Itchy;

public class Util
{
    private static final Random random = new Random();

    /**
     * A random chance. For a one in size chance :
     * 
     * <pre>
     * <code>
     *  if ( Util.oneIn( 6 ) ) {
     *     // Do something
     *  }
     * </code>
     * </pre>
     * 
     * @return
     */
    public static boolean randomOneIn( double times )
    {
        return random.nextDouble() * times < 1.0;
    }

    /**
     * A random change based on time. For something to happen on average once every 3 seconds :
     * 
     * <pre>
     * <code>
     *  if ( Util.onceEvery( 3 ) ) {
     *     // Do something
     *  }
     * </code>
     * </pre>
     * 
     * This method assumes it is being called in a behaviour's tick method. i.e. it will only give
     * the correct chance if it is being called once every frame.
     */
    public static boolean randomOnceEvery( double seconds )
    {
        return random.nextDouble() * seconds * Itchy.singleton.getFrameRate() < 1.0;
    }

    /**
     * Generates a random number - not limited to just whole numbers.
     * @param from
     * @param to
     * @return A random number greater or equal to <code>from<code> but less than <code>to<code>.
     */
    public static double randomBetween( double from, double to )
    {
        return random.nextDouble() * (to - from) + from;
    }

    public static final String randomText( String[] choices )
    {

        int index = random.nextInt(choices.length);
        return choices[index];
    }

}
