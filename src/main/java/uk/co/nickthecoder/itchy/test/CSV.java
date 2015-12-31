/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses CSV files. Each field can be quoted with double quotes ("), and if so, can include double
 * quotes by escaping them (\").
 * 
 * CVS can be written, and all fields are enclosed with double quotes.
 * 
 * If you want to read large documents, its probably wise to avoid the parse method, and only use
 * readLine, to prevent the whole document being read into memory.
 */
public class CSV
{
    public static List<List<String>> parse( String filename )
        throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        List<List<String>> result = new ArrayList<List<String>>();

        String line = null;

        while ((line = in.readLine()) != null) {
            result.add(readLine(line));
        }
        in.close();

        return result;
    }

    public static List<String> readLine( String line )
    {
        List<String> fields = new ArrayList<String>();
        int fromIndex = 0;

        String field = null;

        while (fromIndex < line.length()) {

            if (line.charAt(fromIndex) == '"') {
                // The field is enclosed in quotes.
                
                int end = line.indexOf('"', fromIndex + 1);
                // Skip over all of the escaped quotes ie: \"
                while (line.charAt(end - 1) == '\\') {
                    end = line.indexOf('"', end + 1);
                }
                field = line.substring(fromIndex + 1, end);
                field = field.replaceAll("\\\\", "");
                fromIndex = end + 2;

                // Should really test that end + 1 really is a comma, or we are at the end of the line.
            
            } else {
                
                // The field does NOT use quotes.
                int comma = line.indexOf(',', fromIndex + 1);
                if (comma > 0) {
                    field = line.substring(fromIndex, comma);
                    fromIndex = comma + 1;
                } else {
                    field = line.substring(fromIndex);
                    fromIndex = line.length();
                }
            }
            fields.add(field);
        }

        return fields;
    }

    public static void printCVS( PrintStream out, List<List<String>> stuff )
    {
        for (List<String> line : stuff) {
            boolean first = true;
            for (String item : line) {
                if (!first) {
                    System.out.print(",");
                }
                first = false;
                out.print("\"" + item.replaceAll("\"", "\\\\\"") + "\"");
            }
            out.println();
        }
    }

    /**
     * Usage : java -cp BLAH.jar uk.co.nickthecoder.itchy.test.CSV WHATEVER.csv
     */
    public static void main( String[] args )
        throws Exception
    {
        List<List<String>> parsed = CSV.parse(args[0]);
        CSV.printCVS(System.out, parsed);
    }

}
