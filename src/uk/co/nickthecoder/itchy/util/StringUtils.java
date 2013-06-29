package uk.co.nickthecoder.itchy.util;

public class StringUtils
{
    /**
     * Returns true if the string is null, or if it equals ""
     */
    public static boolean isEmpty( String string )
    {
        return ( ( string == null ) || ( string.equals( "" ) ) );
    }

    /**
     * Returns true if the string is null, or if it equals "" after being
     * trimmed.
     */
    public static boolean isBlank( Object object )
    {
        return isBlank( object.toString() );
    }

    public static boolean isBlank( String string )
    {
        return isEmpty( string );
    }

    public static boolean atLeastLength( String value, int length )
    {
        if ( value == null ) {
            return false;
        }

        return ( value.length() >= length );
    }

    /**
     * Pad value with spaces if value is shorter then newLength, truncate it if
     * it is longer.
     */
    public static String fixLength( String value, int newLength )
    {
        if ( value.length() == newLength ) {
            return value;
        } else if ( value.length() < newLength ) {
            StringBuffer buffer = new StringBuffer( value );
            for ( int i = 0; i < newLength - value.length(); i++ ) {
                buffer.append( ' ' );
            }
            return buffer.toString();
        } else {
            return value.substring( 0, newLength );
        }
    }

    /**
     * Looks for all occurances of <i>search</i> and replaces them with
     * <i>replace</i> within the string <i>value</i>. <br>
     * <br>
     * Returns the same string if no replacements were needed, otherwise returns
     * a new string with the appropriate replacements.
     */

    public static String searchAndReplace( String value, String search, String replace )
    {
        if ( value == null ) {
            return null;
        }

        int i = value.indexOf( search );
        if ( i < 0 ) {
            return value;
        }

        int last = 0;
        StringBuffer buffer = new StringBuffer( value.length() + 10 );
        while ( i >= 0 ) {
            buffer.append( value.substring( last, i ) );
            buffer.append( replace );
            last = i + search.length();

            i = value.indexOf( search, last );
        }
        buffer.append( value.substring( last ) );

        return buffer.toString();
    }

    /**
     * Returns the first part of the string, up until (but not including) the
     * first new line. If there is no new line, then the original string is
     * returned.
     */
    public static String firstLine( String string )
    {
        if ( string == null ) {
            return null;
        }

        int index = string.indexOf( "\n" );
        if ( index >= 0 ) {
            return string.substring( 0, index );
        } else {
            return string;
        }
    }

    /**
     * Returns the first part of the string, up until (but not including) the
     * first new line, follwoed by three dots. If there is no new line, then the
     * original string is returned, without any dots.
     */
    public static String firstLineDotDotDot( String string )
    {
        if ( string == null ) {
            return null;
        }

        int index = string.indexOf( "\n" );
        if ( index >= 0 ) {
            return string.substring( 0, index ) + "...";
        } else {
            return string;
        }
    }

}
