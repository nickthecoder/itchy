/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 * 
 * This code is derived from nsSMILKeySpline.cpp, part of the Mozilla code base. Written by Brian
 * Birtles <birtles@gmail.com> It was licensed under the GPL 2.0.
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

public class BezierProfile extends AbstractProfile
{

    private static final int NEWTON_ITERATIONS = 4;
    private static final double NEWTON_MIN_SLOPE = 0.02;
    private static final double SUBDIVISION_PRECISION = 0.0000001;
    private static final int SUBDIVISION_MAX_ITERATIONS = 10;

    private static final int kSplineTableSize = 11;
    private static final double kSampleStepSize = 1.0 / (kSplineTableSize - 1);
    double[] mSampleValues;

    private double mX1, mY1;

    private double mX2, mY2;

    public BezierProfile( double x1, double y1, double x2, double y2 )
    {
        this.mSampleValues = new double[kSplineTableSize];

        this.mX1 = x1;
        this.mY1 = y1;
        this.mX2 = x2;
        this.mY2 = y2;
        if (this.mX1 != this.mY1 || this.mX2 != this.mY2) {
            calcSampleValues();
        }

    }

    @Override
    public double amount( double amount )
    {
        return getSplineValue(amount);
    }

    private double getSplineValue( double aX )
    {
        if ((this.mX1 == this.mY1) && (this.mX2 == this.mY2)) {
            return aX;
        }

        return calcBezier(getTForX(aX), this.mY1, this.mY2);
    }

    private void calcSampleValues()
    {
        for (int i = 0; i < kSplineTableSize; ++i) {
            this.mSampleValues[i] = calcBezier(i * kSampleStepSize, this.mX1, this.mX2);
        }
    }

    private static double A( double aA1, double aA2 )
    {
        return 1.0 - 3.0 * aA2 + 3.0 * aA1;
    }

    private static double B( double aA1, double aA2 )
    {
        return 3.0 * aA2 - 6.0 * aA1;
    }

    private static double C( double aA1 )
    {
        return 3.0 * aA1;
    }

    double calcBezier( double aT, double aA1, double aA2 )
    {
        // use Horner's scheme to evaluate the Bezier polynomial
        return ((A(aA1, aA2) * aT + B(aA1, aA2)) * aT + C(aA1)) * aT;
    }

    private static double getSlope( double aT, double aA1, double aA2 )
    {
        return 3.0 * A(aA1, aA2) * aT * aT + 2.0 * B(aA1, aA2) * aT + C(aA1);
    }

    private double getTForX( double aX )
    {

        // Find interval where t lies
        double intervalStart = 0.0;
        int currentSampleIndex = 1;
        int lastSampleIndex = kSplineTableSize - 1;
        for (; (currentSampleIndex != lastSampleIndex) &&
            (this.mSampleValues[currentSampleIndex] <= aX); currentSampleIndex += 1) {
            intervalStart += kSampleStepSize;
        }

        currentSampleIndex -= 1; // t now lies between *currentSample and *currentSample+1

        // Interpolate to provide an initial guess for t
        double dist = (aX - this.mSampleValues[currentSampleIndex]) /
            (this.mSampleValues[currentSampleIndex + 1] - this.mSampleValues[currentSampleIndex]);
        double guessForT = intervalStart + dist * kSampleStepSize;

        // Check the slope to see what strategy to use. If the slope is too small
        // Newton-Raphson iteration won't converge on a root so we use bisection
        // instead.
        double initialSlope = getSlope(guessForT, this.mX1, this.mX2);
        if (initialSlope >= NEWTON_MIN_SLOPE) {
            return newtonRaphsonIterate(aX, guessForT);
        } else if (initialSlope == 0.0) {
            return guessForT;
        } else {
            return binarySubdivide(aX, intervalStart, intervalStart + kSampleStepSize);
        }
    }

    private double newtonRaphsonIterate( double aX, double aGuessT )
    {
        // Refine guess with Newton-Raphson iteration
        for (int i = 0; i < NEWTON_ITERATIONS; ++i) {
            // We're trying to find where f(t) = aX,
            // so we're actually looking for a root for: CalcBezier(t) - aX
            double currentX = calcBezier(aGuessT, this.mX1, this.mX2) - aX;
            double currentSlope = getSlope(aGuessT, this.mX1, this.mX2);

            if (currentSlope == 0.0) {
                return aGuessT;
            }

            aGuessT -= currentX / currentSlope;
        }

        return aGuessT;
    }

    private double binarySubdivide( double aX, double aA, double aB )
    {
        double currentX;
        double currentT = 0;
        int i = 0;

        for (i = 0; i < SUBDIVISION_MAX_ITERATIONS; i++) {
            currentT = aA + (aB - aA) / 2.0;
            currentX = calcBezier(currentT, this.mX1, this.mX2) - aX;

            if (currentX > 0.0) {
                aB = currentT;
            } else {
                aA = currentT;
            }
            if (Math.abs(currentX) < SUBDIVISION_PRECISION) {
                break;
            }
        }
        return currentT;
    }

}
