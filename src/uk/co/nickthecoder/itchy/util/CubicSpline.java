/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This isn't designed to DRAW curves, its designed to move Actors in a curved path, visiting a
 * number of fixed points along the way. You add each point using the addPoint method, then you
 * calculate the spline, and then you iterate over it.
 */
public class CubicSpline
{
    private List<Float> xs;
    private List<Float> ys;

    private Cubic[] xCubics;
    private Cubic[] yCubics;
    
    public int steps = 10;
    
    public CubicSpline()
    {
        this.xs = new ArrayList<Float>();
        this.ys = new ArrayList<Float>();
    }

    public void add( double x, double y )
    {
        add( (float) x, (float) y );
    }
    
    public void add( float x, float y )
    {
        this.xs.add(x);
        this.ys.add(y);
    }

    private void calculate()
    {
        xCubics = calculateNaturalCubic( xs );
        yCubics = calculateNaturalCubic( ys );
    }
    
    public Iterator<Point2D.Float> iterate()
    {
        if ( xCubics == null ) {
            calculate(); 
        }
        
        return new Iterator<Point2D.Float>() {

            int segment = 0;
            int step = 0;
            
            @Override
            public boolean hasNext()
            {
                return segment < xCubics.length;
            }

            @Override
            public java.awt.geom.Point2D.Float next()
            {
                Point2D.Float result = new Point2D.Float(
                    xCubics[segment].eval( step/(float) steps),
                    yCubics[segment].eval( step/(float) steps));
                
                step += 1;
                if (step > steps) {
                    segment +=1;
                    step = 0;
                }
                
                return result;
            }

            @Override
            public void remove()
            {               
            }
            
        };
    }
    
    public static Cubic[] calculateNaturalCubic( List<Float> values )
    {
        float[] array = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i);
        }
        return calculateNaturalCubic(array);
    }

    /*
     * Calculates the natural cubic spline that interpolates y[0], y[1], ... y[n]. The first segment
     * is returned as C[0].a + C[0].b*u + C[0].c*u^2 + C[0].d*u^3 0<=u <1 the other segments are in
     * C[1], C[2], ... C[n-1]
     */
    public static Cubic[] calculateNaturalCubic( float[] x )
    {
        int n = x.length - 1;

        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;
        /*
         * We solve the equation [2 1 ] [D[0]] [3(x[1] - x[0]) ] |1 4 1 | |D[1]| |3(x[2] - x[0]) | |
         * 1 4 1 | | . | = | . | | ..... | | . | | . | | 1 4 1| | . | |3(x[n] - x[n-2])| [ 1 2]
         * [D[n]] [3(x[n] - x[n-1])]
         * 
         * by using row operations to convert the matrix to upper triangular and then back
         * sustitution. The D[i] are the derivatives at the knots.
         */

        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x[1] - x[0]) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
        }
        delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        /* now compute the coefficients of the cubics */
        Cubic[] C = new Cubic[n];
        for (i = 0; i < n; i++) {
            C[i] = new Cubic(x[i], D[i], 3 * (x[i + 1] - x[i]) - 2 * D[i] - D[i + 1],
                2 * (x[i] - x[i + 1]) + D[i] + D[i + 1]);
        }
        return C;
    }

}
