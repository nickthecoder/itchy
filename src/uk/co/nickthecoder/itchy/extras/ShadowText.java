/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.roles.Follower;
import uk.co.nickthecoder.itchy.roles.Projectile;
import uk.co.nickthecoder.jame.RGBA;

// TODO Replace this with regular text with Makeup.
public class ShadowText
{
    public String text;

    public Font font;

    public int fontSize;

    public RGBA color = RGBA.WHITE;

    public RGBA shadow = RGBA.BLACK;

    public int shadowOffsetX = 4;

    public int shadowOffsetY = 4;


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

    public ImagePose createPose()
    {
        if (this.font == null) {
            this.font = Itchy.getResources().getDefaultFont();
        }
        TextPose text = new TextPose(this.text, this.font, this.fontSize, this.color);
        TextPose shadow = new TextPose(this.text, this.font, this.fontSize, this.shadow);

        ImagePose pose = ImagePose.superimpose( shadow, text, -this.shadowOffsetX, -this.shadowOffsetY);

        return pose;
    }
    
    public Follower follow( Actor source )
    {
        Follower result = new Follower( source );
        result.pose( createPose() );
        return result;
    }
    
    public Projectile projectile( Actor source )
    {
        Projectile projectile = new Projectile( source );
        projectile.pose( createPose() );
        return projectile;
    }

}
