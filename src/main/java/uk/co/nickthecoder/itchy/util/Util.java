/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.Random;

import uk.co.nickthecoder.itchy.Itchy;

public class Util
{
    private static final Random random = new Random();

    /**
     * A random chance. For a one in N chance :
     * 
     * <pre>
     * <code>
     *  if ( Util.randomOneIn( 6 ) ) {
     *     // Do something
     *  }
     * </code>
     * </pre>
     * 
     */
    public static boolean randomOneIn( double n )
    {
        return random.nextDouble() * n < 1.0;
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
     * This method assumes it is being called once every frame. For example, you could call it from a Role's tick method.
     */
    public static boolean randomOnceEvery( double seconds )
    {
        return random.nextDouble() * seconds * Itchy.frameRate.getFrameRate() < 1.0;
    }

    /**
     * Generates a random number - not limited to just whole numbers.
     * 
     * @param from
     * @param to
     * @return A random number greater or equal to <code>from<code> but less than <code>to<code>.
     */
    public static double randomBetween( double from, double to )
    {
        return random.nextDouble() * (to - from) + from;
    }

    /**
     * Picks a string randomly from the array of strings supplied.
     * 
     * @param choices
     * @return One of the strings from <code>choices</code>.
     */
    public static final String randomText( String[] choices )
    {

        int index = random.nextInt(choices.length);
        return choices[index];
    }

    /**
     * @param filename
     *        A filename, or path, which may or may not have a file extension
     * @return The name of the file with the file extension removed. Note, if the file has two extensions, such as "foo.tar.gz", then only
     *         one is stripped, return "foo.tar" in this example.
     */
    public static String nameFromFilename( String filename )
    {
        return nameFromFile(new File(filename));
    }

    /**
     * @param file
     * @return The name of the file with the file extension removed. Note, if the file has two extensions, such as "foo.tar.gz", then only
     *         one is stripped, return "foo.tar" in this example.
     */
    public static String nameFromFile( File file )
    {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 1) {
            return name.substring(0, lastDot);
        }
        return name;
    }

    /**
     * Copies a directory recursively.
     * 
     * @param src
     *        The directory to copy from.
     * @param dest
     *        The directory to be created.
     * @throws IOException
     */
    public static void copyDirectory( File src, File dest )
        throws IOException
    {
        dest.mkdir();
        for (File file : src.listFiles()) {
            if (file.isDirectory()) {
                copyDirectory(file, new File(dest, file.getName()));
            } else {
                copyFile(file, new File(dest, file.getName()));
            }
        }
    }

    /**
     * Copies a single file.
     * 
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile( File sourceFile, File destFile )
        throws IOException
    {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * Reads the contents of a file returning it as a String. Only use this if you know that the file is small, beacause a large file will
     * consume all available memory, which will, at best crash the program and at worst will make the computer so unresponsive that you'll
     * need to reboot it uncleanly.
     * 
     * @param file
     * @return The contents of the file
     * @throws IOException
     */
    public static String readFile( File file )
        throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append('\n');
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    /**
     * A very simple template mechanism, copying one file to another, performing substitutions of the for ${FOO}.
     * 
     * @param templateFile
     *        The source template to copy from.
     * @param destFile
     *        The file to create
     * @param substitutions
     *        A map of substitutions. Wherever ${KEY} is found in the document it is replaced with that key's value.
     * @throws IOException
     */
    public static void template( File templateFile, File destFile, Map<String, String> substitutions )
        throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(
            templateFile)));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(destFile, true)));

        String line = reader.readLine();
        while (line != null) {
            writeTemplateLine(writer, line, substitutions);

            line = reader.readLine();
        }
        reader.close();
        writer.close();

    }

    private static void writeTemplateLine( PrintWriter out, String line,
        Map<String, String> substitutions )
    {
        int fromIndex = 0;
        int open = line.indexOf("${");
        while (open >= 0) {

            out.print(line.substring(fromIndex, open));

            int close = line.indexOf("}", open);
            if (close < 0) {
                out.println("${");
                fromIndex += 2;

            } else {
                String key = line.substring(open + 2, close);
                if (substitutions.containsKey(key)) {
                    out.print(substitutions.get(key));
                } else {
                    out.print("${");
                    out.print(key);
                    out.print("}");
                }
                fromIndex = close + 1;
            }
            open = line.indexOf("${", fromIndex);
        }
        out.println(line.substring(fromIndex));
    }

    public static <T> T[] concatenate( T[] A, T[] B )
    {
        int aLen = A.length;
        int bLen = B.length;

        @SuppressWarnings("unchecked")
        T[] C = (T[]) Array.newInstance(A.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);

        return C;
    }
    
    public static void printStackTrace()
    {
        try {
            throw new Exception("printStackTrace");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
