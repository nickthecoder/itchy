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
    private DoubleBox txtFrom;
    private DoubleBox txtTo;

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

        this.txtFrom = new DoubleBox(scaleAnimation.from);
        gridLayout.addRow(new Label("Scale (at the start)"),
                Editor.addHint(this.txtFrom, "1 = normal size"));

        this.txtTo = new DoubleBox(scaleAnimation.to);
        gridLayout.addRow(new Label("Scale (at the end)"),
                Editor.addHint(this.txtTo, "1 = normal size"));
    }

    @Override
    public boolean save()
    {
        ScaleAnimation scaleAnimation = (ScaleAnimation) this.animation;

        scaleAnimation.ticks = this.txtTicks.getValue();
        scaleAnimation.profile = this.pickProfile.getValue();

        scaleAnimation.from = this.txtFrom.getValue();
        scaleAnimation.to = this.txtTo.getValue();

        return true;
    }

}
