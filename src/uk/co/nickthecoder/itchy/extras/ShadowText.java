/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.jame.RGBA;

public class ShadowText extends Projectile
{
    public static final RGBA WHITE = new RGBA(255, 255, 255, 255);
    public static final RGBA BLACK = new RGBA(0, 0, 0, 255);

    public String text;

    public Font font;

    public int fontSize;

    public RGBA color = WHITE;

    public RGBA shadow = BLACK;

    public int shadowOffsetX = 4;

    public int shadowOffsetY = 4;

    public int offsetX = 0;

    public int offsetY = 0;

    public ShadowText text( String text )
    {
        this.text = text;
        return this;
    }

    public ShadowText font( Font font )
    {
        this.font = font;
        return this;
    }

    public ShadowText font( String name )
    {
        this.font = Itchy.getGame().resources.getFont(name);
        return this;
    }

    public ShadowText fontSize( int value )
    {
        this.fontSize = value;
        return this;
    }

    public ShadowText color( RGBA value )
    {
        this.color = value;
        return this;
    }

    public ShadowText shadow( RGBA value )
    {
        this.shadow = value;
        return this;
    }

    public ShadowText shadowOffset( int dx, int dy )
    {
        this.shadowOffsetX = dx;
        this.shadowOffsetY = dy;
        return this;
    }

    public ShadowText offset( int dx, int dy )
    {
        this.offsetX = dx;
        this.offsetY = dy;
        return this;
    }

    @Override
    public ShadowText speed( double value )
    {
        super.speed(value);
        return this;
    }

    @Override
    public ShadowText vx( double value )
    {
        super.vx(value);
        return this;
    }

    @Override
    public ShadowText vy( double value )
    {
        super.vy(value);
        return this;
    }

    @Override
    public ShadowText gravity( double value )
    {
        super.gravity(value);
        return this;
    }

    @Override
    public ShadowText spin( double value )
    {
        super.spin(value);
        return this;
    }

    @Override
    public ShadowText fade( double value )
    {
        super.fade(value);
        return this;
    }

    @Override
    public ShadowText growFactor( double value )
    {
        super.growFactor(value);
        return this;
    }

    
    public Actor createActor( Actor source )
    {
        if (this.font == null) {
            this.font = Itchy.getResources().getDefaultFont();
        }
        TextPose text = new TextPose(this.text, this.font, this.fontSize, this.color);
        TextPose shadow = new TextPose(this.text, this.font, this.fontSize, this.shadow);

        Actor actor = new Actor(shadow);
        actor.getAppearance().superimpose(text, -this.shadowOffsetX, -this.shadowOffsetY);

        actor.moveTo(source);
        actor.moveBy(this.offsetX, this.offsetY);
        actor.moveBy(this.shadowOffsetX, this.shadowOffsetY);
        actor.setBehaviour(this);
        source.getLayer().add(actor);
        return actor;
    }

}
