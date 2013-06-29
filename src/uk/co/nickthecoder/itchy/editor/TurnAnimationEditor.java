package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.animation.NumericAnimation;
import uk.co.nickthecoder.itchy.animation.Profile;
import uk.co.nickthecoder.itchy.animation.TurnAnimation;
import uk.co.nickthecoder.itchy.gui.DoubleBox;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;

public class TurnAnimationEditor extends AnimationEditor
{
    private IntegerBox txtTicks;
    private PickerButton<Profile> pickProfile;
    private DoubleBox txtFrom;
    private DoubleBox txtTo;

    public TurnAnimationEditor( Editor editor, TurnAnimation animation )
    {
        super( editor, animation );
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        TurnAnimation turnAnimation = (TurnAnimation) this.animation;

        this.txtTicks = new IntegerBox( turnAnimation.ticks );
        gridLayout.addRow( new Label( "Duration" ), Editor.addHint( this.txtTicks, "frames" ) );

        this.pickProfile = new PickerButton<Profile>( "Profile", turnAnimation.profile, NumericAnimation.getProfiles() );
        gridLayout.addRow( new Label( "Profile" ), this.pickProfile );

        this.txtFrom = new DoubleBox( turnAnimation.from );
        gridLayout.addRow( new Label( "Turn by (at the start)" ), Editor.addHint( this.txtFrom, "degrees per frame" ) );

        this.txtTo = new DoubleBox( turnAnimation.to );
        gridLayout.addRow( new Label( "Turn by (at the end)" ), Editor.addHint( this.txtTo, "degrees per frame" ) );
    }

    @Override
    public boolean save()
    {
        TurnAnimation turnAnimation = (TurnAnimation) this.animation;

        turnAnimation.ticks = this.txtTicks.getValue();
        turnAnimation.profile = this.pickProfile.getValue();

        turnAnimation.from = this.txtFrom.getValue();
        turnAnimation.to = this.txtTo.getValue();

        return true;
    }

}
