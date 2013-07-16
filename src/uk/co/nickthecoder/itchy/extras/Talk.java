/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.extras;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.util.BorderPoseDecorator;
import uk.co.nickthecoder.itchy.util.NinePatch;

import uk.co.nickthecoder.jame.RGBA;

public class Talk extends Follower
{
    private static RGBA DEFAULT_COLOR = new RGBA(255,255,255);
    
    
    private String text;

    private String bubbleName = "speechBubble";

    private Font font;

    private int fontSize;

    private RGBA color = DEFAULT_COLOR;
    
    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;
    private int marginLeft = 0;

    public Talk( Behaviour following )
    {
        this(following.getActor());
    }

    public Talk( Actor following )
    {
        super(following);
    }

    public Talk text( String text )
    {
        this.text = text;
        return this;
    }
    
    public Talk message( String message )
    {
        String text = this.following.getCostume().getString(message);
        if (text == null) {
            this.text = message;
        } else {
            this.text = text;
        }
        return this;
    }

    
    public Talk bubble( String name )
    {
        this.bubbleName = name;
        return this;
    }

    public Talk font( String fontName )
    {
        Font font = Itchy.singleton.getResources().getFont(fontName);
        return font(font);
    }

    public Talk font( Font font )
    {
        this.font = font;
        return this;
    }

    public Talk font( String fontName, int fontSize )
    {
        Font font = Itchy.singleton.getResources().getFont(fontName);
        return font(font,fontSize);
    }
    
    public Talk font( Font font, int fontSize )
    {
        this.font = font;
        this.fontSize = fontSize;
        return this;
    }

    public Talk fontSize( int fontSize )
    {
        this.fontSize = fontSize;
        return this;
    }

    public Talk color( RGBA color )
    {
        this.color = color;
        return this;
    }
    
    public Talk margin( int margin )
    {
        this.marginTop = margin;
        this.marginRight = margin;
        this.marginBottom = margin;
        this.marginLeft = margin;
        return this;
    }

    public Talk margin( int topBottom, int leftRight )
    {
        this.marginTop = topBottom;
        this.marginRight = leftRight;
        this.marginBottom = topBottom;
        this.marginLeft = leftRight;
        return this;
    }

    public Talk margin( int top, int right, int bottom, int left )
    {
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
        this.marginLeft = left;
        return this;
    }

    public Talk offset( double x, double y )
    {
        super.offset(x,  y );
        return this;
    }

    @Override
    public Actor createActor()
    {
        NinePatch bubble = Itchy.singleton.getResources().getNinePatch(this.bubbleName);

        if (this.font == null) {
            this.font = Itchy.singleton.getResources().getDefaultFont();
        }
        Pose pose = new TextPose(this.text, this.font, this.fontSize, this.color);

        if (bubble != null) {
            BorderPoseDecorator bpd = new BorderPoseDecorator(
                bubble,
                this.marginTop,
                this.marginRight,
                this.marginBottom,
                this.marginLeft);

            pose = bpd.createPose(pose);
        }

        return super.createActor(pose);
    }
}
