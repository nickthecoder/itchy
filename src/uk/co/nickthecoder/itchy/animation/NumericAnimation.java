/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.HashMap;

import uk.co.nickthecoder.itchy.Actor;

public abstract class NumericAnimation extends AbstractAnimation
{
    private static HashMap<String, Profile> profiles;

    public static final Profile linear = new LinearProfile();
    public static final Profile bounce = new BounceProfile();
    public static final Profile unit = new ConstantProfile(1);

    private double previous;
    
    public static HashMap<String, Profile> getProfiles()
    {
        if (profiles == null) {
            profiles = new HashMap<String, Profile>();
            profiles.put("linear", linear);
            profiles.put("bounce", bounce);
            profiles.put("unit", unit);
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

    public int ticks;

    public Profile profile;

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
