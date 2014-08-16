/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Role;

public class Projectile extends Companion
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

    public static abstract class AbstractProjectileBuilder<C extends Projectile, B extends AbstractProjectileBuilder<C, B>>
        extends AbstractCompanionBuilder<C, B>
    {

        public B speed( double value )
        {
            this.companion.speedForwards = value;
            return getThis();
        }

        public B speed( double forwards, double sidewards )
        {
            this.companion.speedForwards = forwards;
            this.companion.speedSidewards = sidewards;
            return getThis();
        }

        public B vx( double value )
        {
            this.companion.vx = value;
            return getThis();
        }

        public B vy( double value )
        {
            this.companion.vy = value;
            return getThis();
        }

        public B gravity( double value )
        {
            this.companion.gravity = value;
            return getThis();
        }

        public B spin( double value )
        {
            this.companion.spin = value;
            return getThis();
        }

        public B fade( double value )
        {
            this.companion.fade = value;
            return getThis();
        }

        public B growFactor( double value )
        {
            this.companion.growFactor = value;
            return getThis();
        }

        public B life( double seconds )
        {
            this.companion.lifeTicks = (int) (Itchy.frameRate.getRequiredRate() * seconds);
            return getThis();
        }

    }

    public static ProjectileBuilder builder(Actor actor)
    {
        return new ProjectileBuilder(actor);
    }

}
