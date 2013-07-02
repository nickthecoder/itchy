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
    private DoubleBox txtFrom;
    private DoubleBox txtTo;

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

        this.txtFrom = new DoubleBox(alphaAnimation.from);
        gridLayout.addRow(new Label("Start Alpha"),
                Editor.addHint(this.txtFrom, "0 = transparent. 255 = opaque"));

        this.txtTo = new DoubleBox(alphaAnimation.to);
        gridLayout.addRow(new Label("End Alpha"),
                Editor.addHint(this.txtTo, "0 = transparent. 255 = opaque"));
    }

    @Override
    public boolean save()
    {
        AlphaAnimation alphaAnimation = (AlphaAnimation) this.animation;

        alphaAnimation.ticks = this.txtTicks.getValue();
        alphaAnimation.profile = this.pickProfile.getValue();

        alphaAnimation.from = this.txtFrom.getValue();
        alphaAnimation.to = this.txtTo.getValue();

        return true;
    }

}
