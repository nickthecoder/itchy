/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class MoveAnimationEditor extends AnimationEditor
{
    private IntegerBox txtTicks;
    private PickerButton<Profile> pickProfile;
    private DoubleBox txtDx;
    private DoubleBox txtDy;

    public MoveAnimationEditor( Editor editor, MoveAnimation animation )
    {
        super(editor, animation);
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        MoveAnimation moveAnimation = (MoveAnimation) this.animation;

        this.txtTicks = new IntegerBox(moveAnimation.ticks);
        gridLayout.addRow(new Label("Duration"), Editor.addHint(this.txtTicks, "frames"));

        this.pickProfile = new PickerButton<Profile>("Profile", moveAnimation.profile,
                NumericAnimation.getProfiles());
        gridLayout.addRow(new Label("Profile"), this.pickProfile);

        this.txtDx = new DoubleBox(moveAnimation.dx);
        gridLayout.addRow(new Label("Delta X"), Editor.addHint(this.txtDx, "pixels per frame"));

        this.txtDy = new DoubleBox(moveAnimation.dy);
        gridLayout.addRow(new Label("Delta Y"), Editor.addHint(this.txtDy, "pixels per frame"));
    }

    @Override
    public boolean save()
    {
        MoveAnimation moveAnimation = (MoveAnimation) this.animation;

        moveAnimation.ticks = this.txtTicks.getValue();
        moveAnimation.profile = this.pickProfile.getValue();

        moveAnimation.dx = this.txtDx.getValue();
        moveAnimation.dy = this.txtDy.getValue();

        return true;
    }

}
