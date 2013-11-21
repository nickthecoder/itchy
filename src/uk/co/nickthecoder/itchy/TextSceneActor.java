/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import uk.co.nickthecoder.jame.RGBA;

public class TextSceneActor extends SceneActor
{
    public Font font;

    public int fontSize;

    public String text;

    public RGBA color;

    public double xAlignment;

    public double yAlignment;

    public Costume costume;

    protected TextSceneActor( Actor actor )
    {
        super(actor);
        TextPose pose = (TextPose) actor.getAppearance().getPose();
        this.font = pose.getFont();
        this.fontSize = (int) pose.getFontSize();
        this.text = pose.getText();
        this.color = new RGBA(pose.getColor());
        this.costume = actor.getCostume();
        this.xAlignment = pose.getXAlignment();
        this.yAlignment = pose.getYAlignment();
    }

    public TextSceneActor( Font font, int fontSize, String text )
    {
        this.font = font;
        this.fontSize = fontSize;
        this.text = text;
        this.color = new RGBA(255, 255, 255);
        this.costume = null;
        this.xAlignment = 0.5;
        this.yAlignment = 0.5;
    }

    @Override
    public Actor createActor( Resources resources, boolean designActor )
    {
        TextPose pose = new TextPose(this.text, this.font, this.fontSize);
        pose.setColor(this.color);
        pose.setAlignment(this.xAlignment, this.yAlignment);
        Actor actor = new Actor(pose);
        if (this.costume != null) {
            actor.setCostume(this.costume);
        }

        this.updateActor(actor, resources, designActor);

        return actor;
    }

}
