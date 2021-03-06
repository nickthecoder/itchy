/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.DoubleProperty;

public class Bouncy extends AbstractRole
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new DoubleProperty<Role>("vx"));
        properties.add(new DoubleProperty<Role>("vy"));
        properties.add(new DoubleProperty<Role>("mass"));
    }
        
    public static final String[] BOUNCY_LIST = new String[] { "bouncy" };

    public double vx = 0;

    public double vy = 0;

    public double mass = 1;

    public double radius = 20;


    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onAttach()
    {
        super.onAttach();
        addTag("bouncy");
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        removeTag("bouncy");
    }

    @Override
    public void tick()
    {
        getActor().moveBy(this.vx, this.vy);

        double radius = this.radius * getActor().getAppearance().getScale();

        if ((this.vy) > 0 && (getActor().getY() + radius > 480)) {
            this.vy = -this.vy;
        }
        if ((this.vx) > 0 && (getActor().getX() + radius > 640)) {
            this.vx = -this.vx;
        }
        if ((this.vy) < 0 && (getActor().getY() - radius < 0)) {
            this.vy = -this.vy;
        }
        if ((this.vx) < 0 && (getActor().getX() - radius < 0)) {
            this.vx = -this.vx;
        }

        getCollisionStrategy().update();

        for (Role role : collisions(BOUNCY_LIST)) {
            collide(getActor(), role.getActor());
        }

    }

    public static void collide( Actor a, Actor b )
    {
        Bouncy bba = (Bouncy) a.getRole();
        Bouncy bbb = (Bouncy) b.getRole();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt(dx * dx + dy * dy);

        double dvx = bbb.vx - bba.vx;
        double dvy = bbb.vy - bba.vy;

        // The speed of the collision in the direction of the line between their centres.
        double collision = (dvx * dx + dvy * dy) / dist;

        if (collision < 0) {
            // They are moving away from each other
            return;
        }

        // Assume mass goes up by the cube of its size (which is appropriate for a 3D object).
        // Maybe it should be my the square, if we think they are only 2D shapes!
        double scaleA = bba.getActor().getAppearance().getScale();
        double scaleB = bbb.getActor().getAppearance().getScale();
        double massA = bba.mass * scaleA * scaleA * scaleA;
        double massB = bbb.mass * scaleB * scaleB * scaleB;

        double massSum = massA + massB;

        bba.vx += dx / dist * collision * 2 * massB / massSum;
        bbb.vx -= dx / dist * collision * 2 * massA / massSum;

        bba.vy += dy / dist * collision * 2 * massB / massSum;
        bbb.vy -= dy / dist * collision * 2 * massA / massSum;

    }

    public static void collideOld( Actor a, Actor b )
    {

        Bouncy bba = (Bouncy) a.getRole();
        Bouncy bbb = (Bouncy) b.getRole();

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        double dist = Math.sqrt(dx * dx + dy * dy);

        double dvx = bbb.vx - bba.vx;
        double dvy = bbb.vy - bba.vy;

        double collision = (dvx * dx + dvy * dy) / dist;

        if (collision < 0) {
            // They are moving away from each other
            return;
        }

        bba.vx += dx / dist * collision;
        bbb.vx -= dx / dist * collision;

        bba.vy += dy / dist * collision;
        bbb.vy -= dy / dist * collision;

    }

}
