/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;

public class Projectile extends Companion<Projectile>
{
    public static final double DEFAULT_LIFE_SECONDS = 6;

    public double gravity;

    public double vx;

    public double vy;

    public double speedForwards;

    public double speedSidewards;

    public double spin;

    public double fade;

    public double growFactor = 1;

    public int lifeTicks;

    public Projectile( Role source )
    {
        this(source.getActor());
    }

    public Projectile( Actor source )
    {
        super(source);
        this.lifeTicks = (int) (DEFAULT_LIFE_SECONDS * Itchy.frameRate.getRequiredRate());
    }

    public Projectile speed( double value )
    {
        this.speedForwards = value;
        return this;
    }

    public Projectile speed( double forwards, double sidewards )
    {
        this.speedForwards = forwards;
        this.speedSidewards = sidewards;
        return this;
    }

    public Projectile vx( double value )
    {
        this.vx = value;
        return this;
    }

    public Projectile vy( double value )
    {
        this.vy = value;
        return this;
    }

    public Projectile gravity( double value )
    {
        this.gravity = value;
        return this;
    }

    public Projectile spin( double value )
    {
        this.spin = value;
        return this;
    }

    public Projectile fade( double value )
    {
        this.fade = value;
        return this;
    }

    public Projectile growFactor( double value )
    {
        this.growFactor = value;
        return this;
    }

    public Projectile life( double seconds )
    {
        this.lifeTicks = (int) (Itchy.frameRate.getRequiredRate() * seconds);
        return this;
    }

    @Override
    public void tick()
    {
        double ox = getActor().getX();
        double oy = getActor().getY();

        getActor().moveBy(this.vx, this.vy);
        getActor().moveForwards(this.speedForwards, this.speedSidewards);

        // Turn the image in the direction of movement.
        if ((this.spin == 0) && this.rotate && (this.gravity != 0)) {
            double dx = getActor().getX() - ox;
            double dy = getActor().getY() - oy;
            double angle = Math.atan2(dy, dx);
            getActor().getAppearance().setDirectionRadians(angle);
        }

        this.vy += this.gravity;

        getActor().getAppearance().adjustAlpha(-this.fade);
        getActor().getAppearance().adjustDirection(this.spin);

        if (this.growFactor != 1) {
            getActor().getAppearance().setScale(getActor().getAppearance().getScale() * this.growFactor);
        }

        if ((this.lifeTicks-- < 0) || (getActor().getAppearance().getAlpha() <= 0)) {
            getActor().kill();
        }
    }

}
