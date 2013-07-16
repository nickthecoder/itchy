/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class AlphaAnimationEditor extends AnimationEditor
{
    private IntegerBox txtTicks;
    private PickerButton<Profile> pickProfile;
    private DoubleBox txtTarget;

    public AlphaAnimationEditor( Editor editor, AlphaAnimation animation )
    {
        super(editor, animation);
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        AlphaAnimation alphaAnimation = (AlphaAnimation) this.animation;

        this.txtTicks = new IntegerBox(alphaAnimation.ticks);
        gridLayout.addRow(new Label("Duration"), Editor.addHint(this.txtTicks, "frames"));

        this.pickProfile = new PickerButton<Profile>("Profile", alphaAnimation.profile,
                NumericAnimation.getProfiles());
        gridLayout.addRow(new Label("Profile"), this.pickProfile);

        this.txtTarget = new DoubleBox(alphaAnimation.target);
        gridLayout.addRow(new Label("Target Alpha"),
                Editor.addHint(this.txtTarget, "0 = transparent. 255 = opaque"));

    }

    @Override
    public boolean save()
    {
        AlphaAnimation alphaAnimation = (AlphaAnimation) this.animation;

        alphaAnimation.ticks = this.txtTicks.getValue();
        alphaAnimation.profile = this.pickProfile.getValue();

        alphaAnimation.target = this.txtTarget.getValue();

        return true;
    }

}
