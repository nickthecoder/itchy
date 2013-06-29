package uk.co.nickthecoder.itchy.util;

import java.util.Random;

public class Util
{
    private static final Random random = new Random();

    public static final String randomText( String[] choices )
    {

        int index = random.nextInt( choices.length );
        return choices[index];
    }

}
