/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.ProfilePickerButton;
import uk.co.nickthecoder.itchy.animation.TurnAnimation;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;

public class TurnAnimationEditor extends AnimationEditor
{
    private IntegerBox txtTicks;
    private ProfilePickerButton pickProfile;
    private DoubleBox txtTurn;

    public TurnAnimationEditor( Editor editor, TurnAnimation animation )
    {
        super(editor, animation);
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        TurnAnimation turnAnimation = (TurnAnimation) this.animation;

        this.txtTicks = new IntegerBox(turnAnimation.ticks);
        gridLayout.addRow(new Label("Duration"), Editor.addHint(this.txtTicks, "frames"));

        this.pickProfile = new ProfilePickerButton( turnAnimation.profile );
        gridLayout.addRow(new Label("Profile"), this.pickProfile);

        this.txtTurn = new DoubleBox(turnAnimation.turn);
        gridLayout.addRow(new Label("Turn"), Editor.addHint(this.txtTurn, "degrees"));

        super.createForm(gridLayout);
    }

    @Override
    public boolean save()
    {
        super.save();
        try {
            TurnAnimation turnAnimation = (TurnAnimation) this.animation;
    
            turnAnimation.ticks = this.txtTicks.getValue();
            turnAnimation.profile = this.pickProfile.getValue();
    
            turnAnimation.turn = this.txtTurn.getValue();
    
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
