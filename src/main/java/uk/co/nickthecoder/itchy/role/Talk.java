/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.role;

import uk.co.nickthecoder.itchy.AbstractTextPose;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.MultiLineTextPose;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.TextStyle;
import uk.co.nickthecoder.itchy.makeup.PictureFrame;
import uk.co.nickthecoder.itchy.makeup.ScaledBackground;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.jame.RGBA;

public class Talk extends Follower
{
    public String text = "";

    public String bubbleName = null;

    public TextStyle textStyle;

    public Talk(Actor following)
    {
        super(following);
        createTextStyle();
        this.textStyle = new TextStyle();
    }

    private final void createTextStyle()
    {
        this.textStyle = new TextStyle(Itchy.getGame().resources.getDefaultFont(), 14);
    }

    public static abstract class AbstractTalkBuilder<C extends Talk, B extends AbstractTalkBuilder<C, B>> extends
        AbstractFollowerBuilder<C, B>
    {
        public B text(String text)
        {
            this.companion.text = text;
            return getThis();
        }

        public B event(String eventName)
        {
            TextStyle style = this.companion.costume.getTextStyle(eventName).clone();
            if (style != null) {
                this.companion.textStyle = style;
            }
            return getThis();
        }

        public B font(String fontName, int fontSize)
        {
            this.companion.textStyle.setFont(Itchy.getGame().resources.getFont(fontName));
            this.companion.textStyle.fontSize = fontSize;
            return getThis();
        }

        public B textStyle(TextStyle textStyle)
        {
            this.companion.textStyle = textStyle;
            return getThis();
        }

        public B textStyle(String textStyleName)
        {
            this.companion.textStyle = this.companion.source.getCostume().getTextStyle(textStyleName);
            return getThis();
        }

        public B style(String style)
        {
            this.textStyle(style);

            // MORE - We are using STRINGS to redirect to a Pose or a NinePatch,
            // but what we should be doing is
            // let the Costume have the Pose or NinePatch. But Costumes don't
            // have NinePatch events at the moment.
            // Also it may be confusing to have an event called "talk" with a
            // Pose, because we want that pose to be used for
            // the speech bubble, not to change the actor's pose! So we'd need
            // two event names.
            String name = this.companion.source.getCostume().getString(style);
            if (name == null) {
                name = style;
            }
            this.bubble(name);

            return getThis();
        }

        @Override
        public B eventName(String eventName)
        {
            super.eventName(eventName);

            String text = this.companion.source.getCostume().getString(eventName);
            if (text == null) {
                this.companion.text = eventName;
            } else {
                this.companion.text = text;
            }

            return getThis();
        }

        public B bubble(String name)
        {
            this.companion.bubbleName = name;
            return getThis();
        }

        public B alignment(double x, double y)
        {
            this.companion.textStyle.xAlignment = x;
            this.companion.textStyle.yAlignment = y;
            return getThis();
        }

        public B color(RGBA color)
        {
            this.companion.textStyle.color = color;
            return getThis();
        }

        /**
         * Sets the margins of the text within the bubble.
         * 
         * @param margin
         *            The margin of top,left,bottom and right.
         * @return this
         */
        public B margin(int margin)
        {
            this.companion.textStyle.marginTop = margin;
            this.companion.textStyle.marginRight = margin;
            this.companion.textStyle.marginBottom = margin;
            this.companion.textStyle.marginLeft = margin;
            return getThis();
        }

        /**
         * Sets the margins of the text within the bubble.
         * 
         * @param topBottom
         *            The margin of top and bottom.
         * @return this
         */
        public B margin(int topBottom, int leftRight)
        {
            this.companion.textStyle.marginTop = topBottom;
            this.companion.textStyle.marginRight = leftRight;
            this.companion.textStyle.marginBottom = topBottom;
            this.companion.textStyle.marginLeft = leftRight;
            return getThis();
        }

        /**
         * Sets the margins of the text within the bubble.
         * 
         * @return this
         */
        public B margin(int top, int right, int bottom, int left)
        {
            this.companion.textStyle.marginTop = top;
            this.companion.textStyle.marginRight = right;
            this.companion.textStyle.marginBottom = bottom;
            this.companion.textStyle.marginLeft = left;
            return getThis();
        }

        @Override
        public B offset(double x, double y)
        {
            super.offset(x, y);
            return getThis();
        }

        @Override
        public C create()
        {
            if (companion.textStyle == null) {
                Font font = Itchy.getGame().resources.getDefaultFont();
                companion.textStyle = new TextStyle(font, 14);
            }

            AbstractTextPose pose;
            if (companion.text.contains("\n")) {
                pose = new MultiLineTextPose(companion.textStyle);
                pose.setText(companion.text);
            } else {
                pose = new TextPose(companion.text, companion.textStyle);
            }
            companion.pose = pose;

            Actor result = super.create().getActor();

            // Apply the background. Use a Frame if the bubble name is a nine
            // patch, otherwise use a ScaledBackground.
            NinePatch ninePatch = null;
            if (companion.bubbleName != null) {
                ninePatch = Itchy.getGame().resources.getNinePatch(companion.bubbleName);
            }
            if (ninePatch == null) {
                PoseResource backgroundPR = Itchy.getGame().resources.getPoseResource(companion.bubbleName);
                if (backgroundPR != null) {
                    ScaledBackground scaledBackground = new ScaledBackground(companion.textStyle.marginTop,
                        companion.textStyle.marginRight, companion.textStyle.marginBottom,
                        companion.textStyle.marginLeft);
                    scaledBackground.setPoseResource(backgroundPR);
                    result.getAppearance().setMakeup(scaledBackground);
                }
            } else {
                PictureFrame frame = new PictureFrame(companion.textStyle.marginTop, companion.textStyle.marginRight,
                    companion.textStyle.marginBottom, companion.textStyle.marginLeft);
                frame.setNinePatch(ninePatch);
                result.getAppearance().setMakeup(frame);
            }

            result.getAppearance().fixAppearance();
            ImagePose imagePose = (ImagePose) result.getAppearance().getPose();
            imagePose.setOffsetX((int) (imagePose.getSurface().getWidth() * companion.textStyle.xAlignment));
            imagePose.setOffsetY((int) (imagePose.getSurface().getHeight() * companion.textStyle.yAlignment));

            result.setZOrder(companion.source.getZOrder());

            return companion;
        }

    }

}
