/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.gui;

public class StyleCriteria
{
    public String type;
    public String style;
    public boolean wildcard;

    public StyleCriteria( String type, String style )
    {
        this.type = type;
        this.style = style;
        this.wildcard = false;
    }

    public StyleCriteria( boolean wildcard )
    {
        this.wildcard = wildcard;
        this.type = null;
        this.style = null;
    }

    @Override
    public String toString()
    {
        if (this.wildcard) {
            return "*";
        }
        if (this.type == null) {
            if (this.style == null) {
                return "?";
            }
            return "." + this.style;
        }
        if (this.style == null) {
            return this.type;
        } else {
            return this.type + "." + this.style;
        }
    }
}
