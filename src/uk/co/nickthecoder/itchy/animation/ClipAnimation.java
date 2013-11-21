/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.animation;

import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;

public class ClipAnimation extends NumericAnimation
{
    private static final List<AbstractProperty<Animation, ?>> properties =
        AbstractProperty.<Animation> findAnnotations(ClipAnimation.class);

    @Property(label = "Top")
    public double top;

    @Property(label = "Right")
    public double right;

    @Property(label = "Bottom")
    public double bottom;

    @Property(label = "Left")
    public double left;

    private int startTop;
    private int startRight;
    private int startBottom;
    private int startLeft;

    public ClipAnimation()
    {
        this(200, Eases.linear, 0, 0, 0, 0);
    }

    public ClipAnimation( int ticks, Ease ease, double top, double right, double bottom, double left )
    {
        super(ticks, ease);
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    @Override
    public List<AbstractProperty<Animation, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public String getName()
    {
        return "Clip";
    }

    @Override
    public void start( Actor actor )
    {
        super.start(actor);
        Rect startClip = actor.getAppearance().getClip();
        if (startClip == null) {
            this.startTop = this.startRight = this.startBottom = this.startLeft = 0;
        } else {
            Surface surface = actor.getAppearance().getPose().getSurface();
            this.startTop = startClip.y;
            this.startLeft = startClip.x;
            this.startRight = surface.getWidth() - (startClip.x + startClip.width);
            this.startBottom = surface.getHeight() - (startClip.y - startClip.height);
        }

    }

    @Override
    public void tick( Actor actor, double amount, double delta )
    {
        double top = this.startTop + (this.top - this.startTop) * amount;
        double right = this.startRight + (this.right - this.startRight) * amount;
        double bottom = this.startBottom + (this.bottom - this.startBottom) * amount;
        double left = this.startLeft + (this.left - this.startLeft) * amount;

        Rect rect = new Rect((int) top, (int) left, (int) (bottom - top), (int) (right - left));
        if ((left <= 0) && (top <= 0)) {
            Surface surface = actor.getAppearance().getPose().getSurface();
            if ((left + rect.width >= surface.getWidth()) && (top + rect.height >= surface.getHeight())) {
                actor.getAppearance().setClip(null);
                return;
            }
        }
        actor.getAppearance().setClip(rect);
    }

}
