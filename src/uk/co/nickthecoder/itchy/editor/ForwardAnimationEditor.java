/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.ForwardsAnimation;
import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class ForwardAnimationEditor extends AnimationEditor
{
    private IntegerBox txtTicks;
    private PickerButton<Profile> pickProfile;
    private DoubleBox txtForwards;
    private DoubleBox txtSidewards;

    public ForwardAnimationEditor( Editor editor, ForwardsAnimation animation )
    {
        super(editor, animation);
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        ForwardsAnimation forwardsAnimation = (ForwardsAnimation) this.animation;

        this.txtTicks = new IntegerBox(forwardsAnimation.ticks);
        gridLayout.addRow(new Label("Duration"), Editor.addHint(this.txtTicks, "frames"));

        this.pickProfile = new PickerButton<Profile>("Profile", forwardsAnimation.profile,
                NumericAnimation.getProfiles());
        gridLayout.addRow(new Label("Profile"), this.pickProfile);

        this.txtForwards = new DoubleBox(forwardsAnimation.forwards);
        gridLayout.addRow(new Label("Forwards By"), Editor.addHint(this.txtForwards, "(pixels)"));

        this.txtSidewards = new DoubleBox(forwardsAnimation.sideways);
        gridLayout.addRow(new Label("Sidewards By"), Editor.addHint(this.txtSidewards, "(pixels)"));
    
        super.createForm(gridLayout);
    }

    @Override
    public boolean save()
    {
        super.save();
        
        ForwardsAnimation forwardsAnimation = (ForwardsAnimation) this.animation;

        forwardsAnimation.ticks = this.txtTicks.getValue();
        forwardsAnimation.profile = this.pickProfile.getValue();

        forwardsAnimation.forwards = this.txtForwards.getValue();
        forwardsAnimation.sideways = this.txtSidewards.getValue();

        return true;
    }

}
