/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

public class Cubic
{

    float a, b, c, d; /* a + b*u + c*u^2 +d*u^3 */

    public Cubic( float a, float b, float c, float d )
    {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /** evaluate cubic */
    public float eval( float u )
    {
        return (((this.d * u) + this.c) * u + this.b) * u + this.a;
    }
}
