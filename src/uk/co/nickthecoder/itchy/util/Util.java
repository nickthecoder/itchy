/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
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
import java.nio.channels.FileChannel;
import java.util.Map;
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
        return random.nextDouble() * seconds * Itchy.frameRate.getRequiredRate() < 1.0;
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

    public static final String randomText( String[] choices )
    {

        int index = random.nextInt(choices.length);
        return choices[index];
    }

    public static String nameFromFilename( String filename )
    {
        return nameFromFile(new File(filename));
    }

    public static String nameFromFile( File file )
    {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 1) {
            return name.substring(0, lastDot);
        }
        return name;
    }
    

    public static void copyDirectory( File src, File dest )
        throws IOException
    {
        dest.mkdir();
        for ( File file : src.listFiles() ) {
            if (file.isDirectory()) {
                copyDirectory( file, new File(dest, file.getName()));
            } else {
                copyFile( file, new File(dest, file.getName()));
            }
        }
    }
    
    public static void copyFile(File sourceFile, File destFile)
        throws IOException
    {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
    
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

    private static void writeTemplateLine( PrintWriter out, String line, Map<String, String> substitutions )
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
                System.out.println("Found key : " + key);
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

}
