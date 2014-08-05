/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractTextPose;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.FontResource;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.itchy.makeup.Frame;
import uk.co.nickthecoder.itchy.makeup.ScaledBackground;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.RGBA;

public class Talk extends Follower
{
    private String text = "";

    private String bubbleName = "speechBubble";

    private TextStyle textStyle;
    
    private double xAlignment = 0;
    private double yAlignment = 0;

    public Talk( Role following )
    {
        this(following.getActor());
        createTextStyle();
    }

    public Talk( Actor following )
    {
        super(following);
        createTextStyle();
    }

    private final void createTextStyle()
    {
        this.textStyle = new TextStyle(Itchy.getResources().getDefaultFontResource(), 14);
    }

    @Override
    public Talk text( String text )
    {
        this.text = text;
        return this;
    }

    public Talk font( String fontName, int fontSize )
    {
        FontResource fontResource = Itchy.getResources().getFontResource(fontName);
        this.textStyle.fontResource= fontResource;
        this.fontSize = fontSize;
        return this;
    }

    public Talk textStyle( TextStyle textStyle )
    {
        this.textStyle = textStyle;
        return this;
    }

    public Talk textStyle( String textStyleName )
    {
        this.textStyle = this.source.getCostume().getTextStyle(textStyleName);
        return this;
    }

    public Talk style( String style )
    {
        this.textStyle(style);

        // MORE - We are using STRINGS to redirect to a Pose or a NinePatch, but what we should be doing is
        // let the Costume have the Pose or NinePatch. But Costumes don't have NinePatch events at the moment.
        // Also it may be confusing to have an event called "talk" with a Pose, because we want that pose to be used for
        // the speech bubble, not to change the actor's pose! So we'd need two event names.
        String name = this.source.getCostume().getString(style);
        if (name == null) {
            name = style;
        }
        this.bubble(name);

        return this;
    }

    @Override
    public Talk eventName( String eventName )
    {
        super.eventName(eventName);

        String text = this.source.getCostume().getString(eventName);
        if (text == null) {
            this.text = eventName;
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

    public Talk alignment( double x, double y )
    {
        this.xAlignment = x;
        this.yAlignment = y;
        return this;
    }

    @Override
    public Talk color( RGBA color )
    {
        this.color = color;
        return this;
    }

    /**
     * Sets the margins of the text within the bubble.
     * 
     * @param margin
     *        The margin of top,left,bottom and right.
     * @return this
     */
    public Talk margin( int margin )
    {
        this.textStyle.marginTop = margin;
        this.textStyle.marginRight = margin;
        this.textStyle.marginBottom = margin;
        this.textStyle.marginLeft = margin;
        return this;
    }

    /**
     * Sets the margins of the text within the bubble.
     * 
     * @param topBottom
     *        The margin of top and bottom.
     * @return this
     */
    public Talk margin( int topBottom, int leftRight )
    {
        this.textStyle.marginTop = topBottom;
        this.textStyle.marginRight = leftRight;
        this.textStyle.marginBottom = topBottom;
        this.textStyle.marginLeft = leftRight;
        return this;
    }

    /**
     * Sets the margins of the text within the bubble.
     * 
     * @return this
     */
    public Talk margin( int top, int right, int bottom, int left )
    {
        this.textStyle.marginTop = top;
        this.textStyle.marginRight = right;
        this.textStyle.marginBottom = bottom;
        this.textStyle.marginLeft = left;
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
        if (this.textStyle == null) {
            FontResource fontResource = Itchy.getResources().getDefaultFontResource();
            this.textStyle = new TextStyle(fontResource, 14);
        }

        AbstractTextPose pose;
        if (this.text.contains("\n")) {
            pose = new MultiLineTextPose(this.textStyle);
            pose.setText(this.text);
        } else {
            pose = new TextPose(this.text, this.textStyle);
        }
        pose(pose);

        Actor result = super.createActor();

        // Apply the background. Use a Frame if the bubble name is a nine patch, otherwise use a ScaledBackground.
        NinePatch ninePatch = Itchy.getGame().resources.getNinePatch(this.bubbleName);
        if (ninePatch == null) {
            Pose backgroundPose = Itchy.getGame().resources.getPose(this.bubbleName);
            if (backgroundPose == null) {
                System.err.println("Didn't find pose or nine-patch called " + this.bubbleName);
            } else {
                ScaledBackground scaledBackground = new ScaledBackground(
                    this.textStyle.marginTop, this.textStyle.marginRight,
                    this.textStyle.marginBottom, this.textStyle.marginLeft);
                scaledBackground.setPose(backgroundPose);
                result.getAppearance().setMakeup(scaledBackground);
            }
        } else {
            Frame frame = new Frame(
                this.textStyle.marginTop, this.textStyle.marginRight,
                this.textStyle.marginBottom, this.textStyle.marginLeft);
            frame.setNinePatch(ninePatch);
            result.getAppearance().setMakeup(frame);
        }

        result.getAppearance().fixAppearance();
        ImagePose imagePose = (ImagePose) result.getAppearance().getPose();
        imagePose.setOffsetX((int) (imagePose.getSurface().getWidth() * this.xAlignment));
        imagePose.setOffsetY((int) (imagePose.getSurface().getHeight() * this.yAlignment));

        result.setZOrder(this.source.getZOrder());

        return result;
    }
}
