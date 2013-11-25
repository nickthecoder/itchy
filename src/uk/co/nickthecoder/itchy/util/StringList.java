/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

/**
 * Creating comma separated lists of things made easy.
 */
public class StringList
{
    private StringBuffer buffer = new StringBuffer();

    private boolean isEmpty = true;

    private String separator = ", ";

    public boolean isEmpty()
    {
        return this.isEmpty;
    }

    public StringList add( Object item )
    {
        if (!this.isEmpty) {
            this.buffer.append(this.separator);
        }
        this.buffer.append(item.toString());
        isEmpty = false;
        
        return this;
    }

    public StringList separator( String separator )
    {
        this.separator = separator;
        return this;
    }

    @Override
    public String toString()
    {
        return this.buffer.toString();
    }
}
