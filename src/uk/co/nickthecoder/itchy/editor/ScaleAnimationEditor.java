/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class ScaleAnimationEditor extends AnimationEditor
{
    private IntegerBox txtTicks;
    private PickerButton<Profile> pickProfile;
    private DoubleBox txtTarget;

    public ScaleAnimationEditor( Editor editor, ScaleAnimation animation )
    {
        super(editor, animation);
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        ScaleAnimation scaleAnimation = (ScaleAnimation) this.animation;

        this.txtTicks = new IntegerBox(scaleAnimation.ticks);
        gridLayout.addRow(new Label("Duration"), Editor.addHint(this.txtTicks, "frames"));

        this.pickProfile = new PickerButton<Profile>("Profile", scaleAnimation.profile,
                NumericAnimation.getProfiles());
        gridLayout.addRow(new Label("Profile"), this.pickProfile);

        this.txtTarget = new DoubleBox(scaleAnimation.target);
        gridLayout.addRow(new Label("Scale Target"),
                Editor.addHint(this.txtTarget, "1 = Normal Size"));

    }

    @Override
    public boolean save()
    {
        ScaleAnimation scaleAnimation = (ScaleAnimation) this.animation;

        scaleAnimation.ticks = this.txtTicks.getValue();
        scaleAnimation.profile = this.pickProfile.getValue();

        scaleAnimation.target = this.txtTarget.getValue();

        return true;
    }

}
