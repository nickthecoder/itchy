/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.extras.Button;
import uk.co.nickthecoder.itchy.util.Property;
import uk.co.nickthecoder.jame.RGBA;

public class PickLevel extends Button
{
    @Property(label = "Level Number")
    public int levelNumber;

    @Property(label = "Font")
    public Font font;

    @Property(label = "Font Size")
    public int fontSize = 22;

    @Property(label = "Colour")
    public RGBA fontColor = new RGBA(255, 255, 255);

    @Property(label = "Shadow Colour")
    public RGBA shadowColor = new RGBA(0, 0, 0);

    @Override
    public void onActivate()
    {
        if (DrunkInvaders.game.completedLevel(this.levelNumber)) {
            this.getActor().event("completed");
        }

        if (this.font != null) {
            TextPose shadowPose = new TextPose(String.valueOf(this.levelNumber), this.font,
                this.fontSize, this.shadowColor);

            TextPose textPose = new TextPose(String.valueOf(this.levelNumber), this.font,
                this.fontSize, this.fontColor);
            this.getActor().getAppearance().superimpose(shadowPose, 2, 2);
            this.getActor().getAppearance().superimpose(textPose, 0, 0);
        }
    }

    @Override
    public void onClick()
    {
        DrunkInvaders.game.play(this.levelNumber);
    }
}
