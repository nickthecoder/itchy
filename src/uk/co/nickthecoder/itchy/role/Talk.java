/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.makeup.Frame;
import uk.co.nickthecoder.jame.RGBA;

public class Talk extends Follower
{
    private String text;

    private String bubbleName = "speechBubble";

    private Font font;

    private int fontSize = 18;

    private RGBA color = RGBA.WHITE;

    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;
    private int marginLeft = 0;

    public Talk( Role following )
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

    public Talk font( String fontName, int fontSize )
    {
        Font font = Itchy.getResources().getFont(fontName);
        return font(font, fontSize);
    }

    public Talk font( Font font, int fontSize )
    {
        this.font = font;
        this.fontSize = fontSize;
        return this;
    }

    @Override
    public Talk eventName( String eventName )
    {
        super.eventName(eventName);

        if (this.font == null) {
            this.font = this.source.getCostume().getFont(eventName);
        }
        if (this.text == null) {
            String text = this.source.getCostume().getString(eventName);
            if (text == null) {
                this.text = eventName;
            } else {
                this.text = text;
            }
        }

        return this;
    }

    public Talk bubble( String name )
    {
        this.bubbleName = name;
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

    @Override
    public Talk offset( double x, double y )
    {
        super.offset(x, y);
        return this;
    }

    @Override
    public Actor createActor()
    {
        if (this.font == null) {
            this.font = Itchy.getResources().getDefaultFont();
        }
        Pose pose = new TextPose(this.text, this.font, this.fontSize, this.color);
        pose(pose);

        Actor result = super.createActor();

        Frame frame = new Frame(this.marginTop, this.marginRight, this.marginBottom, this.marginLeft);
        frame.setNinePatchName(this.bubbleName);
        result.getAppearance().setMakeup(frame);

        result.setZOrder(this.source.getZOrder());

        return result;
    }
}