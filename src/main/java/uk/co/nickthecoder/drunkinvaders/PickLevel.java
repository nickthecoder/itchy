/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.drunkinvaders;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.property.FontProperty;
import uk.co.nickthecoder.itchy.property.IntegerProperty;
import uk.co.nickthecoder.itchy.property.RGBAProperty;
import uk.co.nickthecoder.itchy.role.Button;
import uk.co.nickthecoder.jame.RGBA;

public class PickLevel extends Button
{
    protected static final List<Property<Role, ?>> properties = new ArrayList<Property<Role, ?>>();

    static {
        properties.add(new IntegerProperty<Role>("levelNumber"));
        properties.add(new FontProperty<Role>("font"));
        properties.add(new IntegerProperty<Role>("fontSize"));
        properties.add(new RGBAProperty<Role>("fontColor"));
        properties.add(new RGBAProperty<Role>("shadowColor"));
    }

    public int levelNumber;

    public Font font;

    public int fontSize = 22;

    public RGBA fontColor = new RGBA(255, 255, 255);

    public RGBA shadowColor = new RGBA(0, 0, 0);

    @Override
    public List<Property<Role, ?>> getProperties()
    {
        return properties;
    }

    @Override
    public void onAttach()
    {
        if (DrunkInvaders.director.completedLevel(this.levelNumber)) {
            getActor().event("completed");
        }

        if (this.font != null) {
            TextPose shadowPose = new TextPose(String.valueOf(this.levelNumber), this.font, this.fontSize,
                            this.shadowColor);

            TextPose textPose = new TextPose(String.valueOf(this.levelNumber), this.font, this.fontSize, this.fontColor);
            getActor().getAppearance().superimpose(shadowPose, 2, 2);
            getActor().getAppearance().superimpose(textPose, 0, 0);
        }
    }

    @Override
    public void onClick()
    {
        DrunkInvaders.director.play(this.levelNumber);
    }
}
