/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Itchy;

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

    public Projectile( Behaviour source )
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
        double ox = this.getActor().getX();
        double oy = this.getActor().getY();

        this.getActor().moveBy(this.vx, this.vy);
        this.getActor().moveForward(this.speedForwards, this.speedSidewards);

        // Turn the image in the direction of movement.
        if ((this.spin == 0) && this.rotate && (this.gravity != 0)) {
            double dx = this.getActor().getX() - ox;
            double dy = this.getActor().getY() - oy;
            double angle = Math.atan2(dy, dx);
            this.getActor().getAppearance().setDirectionRadians(angle);
        }

        this.vy += this.gravity;

        this.getActor().getAppearance().adjustAlpha(-this.fade);
        this.getActor().getAppearance().adjustDirection(this.spin);

        if (this.growFactor != 1) {
            this.getActor().getAppearance().setScale(
                this.getActor().getAppearance().getScale() * this.growFactor);
        }

        if ((this.lifeTicks-- < 0) || (this.getActor().getAppearance().getAlpha() <= 0)) {
            this.getActor().kill();
        }
    }

}
