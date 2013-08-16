/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.util.Property;

public abstract class NumericAnimation extends AbstractAnimation
{
    private static HashMap<String, Profile> profiles;

    public static final Profile linear = new LinearProfile();
    public static final Profile ease = new BezierProfile(0.250, 0.100, 0.250, 1.000);

    public static final Profile easeIn = new BezierProfile(0.420, 0.000, 1.000, 1.000);
    public static final Profile easeInQuad = new BezierProfile(0.550, 0.085, 0.680, 0.530);
    public static final Profile easeInCubic = new BezierProfile(0.895, 0.030, 0.685, 0.220);
    public static final Profile easeInExpo = new BezierProfile(0.950, 0.050, 0.795, 0.035);
    public static final Profile easeInCirc = new BezierProfile(0.600, 0.040, 0.980, 0.335);
    public static final Profile easeInBack = new BezierProfile(0.610, -0.255, 0.730, 0.015);

    public static final Profile easeOut = new BezierProfile(0.000, 0.000, 0.580, 1.000);
    public static final Profile easeOutQuad = new BezierProfile(0.250, 0.460, 0.450, 0.940);
    public static final Profile easeOutCubic = new BezierProfile(0.215, 0.610, 0.355, 1.000);
    public static final Profile easeOutExpo = new BezierProfile(0.190, 1.000, 0.220, 1.000);
    public static final Profile easeOutCirc = new BezierProfile(0.075, 0.820, 0.165, 1.000);
    public static final Profile easeOutBack = new BezierProfile(0.175, 0.885, 0.320, 1.275);

    public static final Profile easeInOut = new BezierProfile(0.420, 0.000, 0.580, 1.000);
    public static final Profile easeInOutQuad = new BezierProfile(0.455, 0.030, 0.515, 0.955);
    public static final Profile easeInOutCubic = new BezierProfile(0.645, 0.045, 0.355, 1.000);
    public static final Profile easeInOutExpo = new BezierProfile(1.000, 0.000, 0.000, 1.000);
    public static final Profile easeInOutCirc = new BezierProfile(0.785, 0.135, 0.150, 0.860);
    public static final Profile easeInOutBack = new BezierProfile( 0.680, -0.550, 0.265, 1.550);
    
    public static final Profile bounce = new CompoundProfile()
        .addProfile(easeInQuad, 1, 1)
        .addProfile(easeOutQuad, 0.2, 0.8)
        .addProfile(easeInQuad, 0.2, 1);

    public static final Profile bounce2 = new CompoundProfile()
        .addProfile(easeInQuad, 1, 1)
        .addProfile(easeOutQuad, 0.2, 0.8)
        .addProfile(easeInQuad, 0.2, 1)
        .addProfile(easeOutQuad, 0.1, 0.95)
        .addProfile(easeInQuad, 0.1, 1);

    public static final Profile bounce3 = new CompoundProfile()
        .addProfile(easeInQuad, 1, 1)
        .addProfile(easeOutQuad, 0.2, 0.8)
        .addProfile(easeInQuad, 0.2, 1)
        .addProfile(easeOutQuad, 0.1, 0.95)
        .addProfile(easeInQuad, 0.1, 1)
        .addProfile(easeOutQuad, 0.05, 0.99)
        .addProfile(easeInQuad, 0.05, 1);


    public static HashMap<String, Profile> getProfiles()
    {
        if (profiles == null) {
            profiles = new HashMap<String, Profile>();
            profiles.put("linear", linear);
            profiles.put("ease", ease);

            profiles.put("easeIn", easeIn);
            profiles.put("easeInQuad", easeInQuad);
            profiles.put("easeInCubic", easeInCubic);
            profiles.put("easeInExpo", easeInExpo);
            profiles.put("easeInCirc", easeInCirc);
            profiles.put("easeInBack", easeInBack);

            profiles.put("easeOut", easeOut);
            profiles.put("easeOutQuad", easeOutQuad);
            profiles.put("easeOutCubic", easeOutCubic);
            profiles.put("easeOutExpo", easeOutExpo);
            profiles.put("easeOutCirc", easeOutCirc);
            profiles.put("easeOutBack", easeOutBack);

            profiles.put("easeInOut", easeOut);
            profiles.put("easeInOutQuad", easeInOutQuad);
            profiles.put("easeInOutCubic", easeInOutCubic);
            profiles.put("easeInOutExpo", easeInOutExpo);
            profiles.put("easeInOutCirc", easeInOutCirc);
            profiles.put("easeInOutBack", easeInOutBack);

            profiles.put("bounce", bounce);
            profiles.put("bounce2", bounce2);
            profiles.put("bounce3", bounce3);

        }

        return profiles;
    }

    public static Profile getProfile( String name )
    {
        return getProfiles().get(name);
    }

    public static String getProfileName( Profile profile )
    {
        for (String name : getProfiles().keySet()) {
            if (getProfiles().get(name) == profile) {
                return name;
            }
        }
        return null;
    }

    
    
    @Property(label="Ticks")
    public int ticks;

    @Property(label="Profile")
    public Profile profile;

    private double previous;

    protected int currentFrame;

    public NumericAnimation( int ticks, Profile profile )
    {
        this.ticks = ticks;
        this.profile = profile;
        this.currentFrame = 0;
    }

    public String getProfileName()
    {
        return getProfileName(this.profile);
    }

    @Override
    public void start( Actor actor )
    {
        this.previous = 0;
        this.currentFrame = 0;
    }

    @Override
    public void tick( Actor actor )
    {
        this.currentFrame++;

        double amount = this.currentFrame / (double) this.ticks;
        double eased = this.profile.amount(amount);
        double delta = eased - this.previous;

        this.tick(actor, eased, delta);
        this.previous = eased;

        super.tick(actor);
    }

    @Override
    public boolean isFinished()
    {
        return this.currentFrame >= this.ticks;
    }

    public abstract void tick( Actor actor, double amount, double delta );

}
