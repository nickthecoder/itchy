/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import uk.co.nickthecoder.itchy.Registry;

public class Eases
{
    public static final Ease linear = new LinearEase();
    public static final Ease defaultEase = new BezierEase(0.250, 0.100, 0.250, 1.000);

    public static final Ease easeIn = new BezierEase(0.420, 0.000, 1.000, 1.000);
    public static final Ease easeInQuad = new BezierEase(0.550, 0.085, 0.680, 0.530);
    public static final Ease easeInCubic = new BezierEase(0.895, 0.030, 0.685, 0.220);
    public static final Ease easeInExpo = new BezierEase(0.950, 0.050, 0.795, 0.035);
    public static final Ease easeInCirc = new BezierEase(0.600, 0.040, 0.980, 0.335);
    public static final Ease easeInBack = new BezierEase(0.610, -0.255, 0.730, 0.015);

    public static final Ease easeOut = new BezierEase(0.000, 0.000, 0.580, 1.000);
    public static final Ease easeOutQuad = new BezierEase(0.250, 0.460, 0.450, 0.940);
    public static final Ease easeOutCubic = new BezierEase(0.215, 0.610, 0.355, 1.000);
    public static final Ease easeOutExpo = new BezierEase(0.190, 1.000, 0.220, 1.000);
    public static final Ease easeOutCirc = new BezierEase(0.075, 0.820, 0.165, 1.000);
    public static final Ease easeOutBack = new BezierEase(0.175, 0.885, 0.320, 1.275);

    public static final Ease easeInOut = new BezierEase(0.420, 0.000, 0.580, 1.000);
    public static final Ease easeInOutQuad = new BezierEase(0.455, 0.030, 0.515, 0.955);
    public static final Ease easeInOutCubic = new BezierEase(0.645, 0.045, 0.355, 1.000);
    public static final Ease easeInOutExpo = new BezierEase(1.000, 0.000, 0.000, 1.000);
    public static final Ease easeInOutCirc = new BezierEase(0.785, 0.135, 0.150, 0.860);
    public static final Ease easeInOutBack = new BezierEase(0.680, -0.550, 0.265, 1.550);

    public static final Ease bounce = new CompoundEase()
        .addEase(easeInQuad, 1, 1)
        .addEase(easeOutQuad, 0.2, 0.8)
        .addEase(easeInQuad, 0.2, 1);

    public static final Ease bounce2 = new CompoundEase()
        .addEase(easeInQuad, 1, 1)
        .addEase(easeOutQuad, 0.2, 0.8)
        .addEase(easeInQuad, 0.2, 1)
        .addEase(easeOutQuad, 0.1, 0.95)
        .addEase(easeInQuad, 0.1, 1);

    public static final Ease bounce3 = new CompoundEase()
        .addEase(easeInQuad, 1, 1)
        .addEase(easeOutQuad, 0.2, 0.8)
        .addEase(easeInQuad, 0.2, 1)
        .addEase(easeOutQuad, 0.1, 0.95)
        .addEase(easeInQuad, 0.1, 1)
        .addEase(easeOutQuad, 0.05, 0.99)
        .addEase(easeInQuad, 0.05, 1);

    public static void registerEases( Registry registry )
    {
        registry.add("linear", linear);
        registry.add("default", defaultEase);

        registry.add("easeIn", easeIn);
        registry.add("easeInQuad", easeInQuad);
        registry.add("easeInCubic", easeInCubic);
        registry.add("easeInExpo", easeInExpo);
        registry.add("easeInCirc", easeInCirc);
        registry.add("easeInBack", easeInBack);

        registry.add("easeOut", easeOut);
        registry.add("easeOutQuad", easeOutQuad);
        registry.add("easeOutCubic", easeOutCubic);
        registry.add("easeOutExpo", easeOutExpo);
        registry.add("easeOutCirc", easeOutCirc);
        registry.add("easeOutBack", easeOutBack);

        registry.add("easeInOut", easeInOut);
        registry.add("easeInOutQuad", easeInOutQuad);
        registry.add("easeInOutCubic", easeInOutCubic);
        registry.add("easeInOutExpo", easeInOutExpo);
        registry.add("easeInOutCirc", easeInOutCirc);
        registry.add("easeInOutBack", easeInOutBack);

        registry.add("bounce", bounce);
        registry.add("bounce2", bounce2);
        registry.add("bounce3", bounce3);

    }
}
