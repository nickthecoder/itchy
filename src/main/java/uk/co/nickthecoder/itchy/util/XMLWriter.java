/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class XMLWriter
{
    /**
     * A large number of spaces, a substring of which are placed at the beginning of the xml lines, to indent them.
     */
    private static String INDENTATION = "                                                          ";

    /**
     */
    private PrintWriter writer;

    /**
     * The number of spaces which cause the xml to be indented. This is increase each time a tag is opened, and descreased each time a tag
     * is closed.
     */
    private int indentSize;

    private boolean firstOnLine;

    private boolean completedOpenTag;

    /**
     */
    public XMLWriter()
    {
    }

    public void begin( String filename ) throws XMLException, IOException
    {
        this.begin(new PrintWriter(new FileOutputStream(filename)));
    }

    public void begin( PrintWriter writer )
    {
        this.writer = writer;

        this.indentSize = 0;
        this.firstOnLine = true;
        this.completedOpenTag = true;
    }

    public PrintWriter getWriter()
    {
        return this.writer;
    }

    public void end()
    {
        this.writer.flush();
        this.writer.close();
    }

    public void beginTag( String tagName )
    {
        // Complete the previous tags open, if it hasn't been
        // completed.
        this.completeOpenTag(true);

        this.print("<");
        this.print(tagName);
        this.indent();

        this.completedOpenTag = false;
    }

    public void endTag( String tagName )
    {
        this.outdent();
        // If the open tag has not been completed, and we are now
        // closing the tag, then the tag does not have a body, so
        if (this.completedOpenTag) {
            this.print("</");
            this.print(tagName);
            this.println(">");
        } else {
            this.println("/>");
        }

        // We are now in the PARENTs tag, whose open tag has been
        // completed (cos we have written some/all of its body)
        this.completedOpenTag = true;
    }

    public void optionalAttribute( String name, String value ) throws XMLException
    {
        if ((name == null) || ("".equals(name))) {
            // Do nothing
        } else {
            this.attribute(name, value);
        }
    }

    public void attribute( String name, String value ) throws XMLException
    {
        if (value == null) {
            value = "";
        } else {
            value = StringUtils.searchAndReplace(value, "&", "&amp;");
            value = StringUtils.searchAndReplace(value, "\"", "&quot;");
            value = StringUtils.searchAndReplace(value, "<", "&lt;");
            value = StringUtils.searchAndReplace(value, "\n", "&#xA;");
        }

        if (this.completedOpenTag) {
            throw new XMLException("Attempted to set an attribute after some of the body.");
        }

        this.print(" ");
        this.print(name);
        this.print("=\"");
        this.print(value);
        this.print("\"");

    }

    public void attribute( String name, boolean value ) throws XMLException
    {
        this.attribute(name, value ? "true" : "false");
    }

    public void attribute( String name, int value ) throws XMLException
    {
        this.attribute(name, Integer.toString(value));
    }

    public void attribute( String name, float value ) throws XMLException
    {
        this.attribute(name, Float.toString(value));
    }

    public void attribute( String name, double value ) throws XMLException
    {
        this.attribute(name, Double.toString(value));
    }

    public void optionalContentTag( String tagName, String content )
    {
        if (!StringUtils.isEmpty(content)) {
            this.contentTag(tagName, content);
        }
    }

    public void contentTag( String tagName, String content )
    {
        if (content == null) {
            content = "";
        }
        this.beginTag(tagName);
        this.body(content);
        this.endTag(tagName);
    }

    public void body( String value )
    {
        this.completeOpenTag(false);

        if (value == null) {
            value = "";
        } else {
            value = StringUtils.searchAndReplace(value, "&", "&amp;");
            value = StringUtils.searchAndReplace(value, "<", "&lt;");
        }

        this.print(value);
    }

    private void completeOpenTag( boolean newLine )
    {
        if (!this.completedOpenTag) {

            this.print(">");
            if (newLine) {
                this.println("");
            }
            this.completedOpenTag = true;
        }
    }

    private void print( String string )
    {
        this.printIndent();
        this.writer.print(string);

    }

    private void println( String string )
    {
        this.printIndent();
        this.writer.println(string);
        this.firstOnLine = true;
    }

    private void printIndent()
    {
        if (this.firstOnLine) {
            while (this.indentSize > INDENTATION.length()) {
                INDENTATION = INDENTATION + INDENTATION;
            }
            if (this.indentSize > 0) {
                this.writer.write(INDENTATION.substring(0, this.indentSize));
            }
        }
        this.firstOnLine = false;
    }

    private void indent()
    {
        this.indentSize += 2;
        if (INDENTATION.length() < this.indentSize) {
            INDENTATION = INDENTATION + "  ";
        }
    }

    private void outdent()
    {
        this.indentSize -= 2;
    }

}
