package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.animation.Frame;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;

public class FramedAnimationEditor extends AnimationEditor
{
    private final Resources resources;

    private List<Frame> frames;

    private Container framesContainer;

    private GridLayout framesGrid;

    public CheckBox chkPingPong;

    public FramedAnimationEditor( Editor editor, Resources resources, FramedAnimation animation )
    {
        super( editor, animation );
        this.resources = resources;
    }

    @Override
    public void createButtons( Container buttonBar )
    {
        Button add = new Button( "Add" );
        add.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                PosePicker posePicker = new PosePicker( FramedAnimationEditor.this.resources )
                {
                    @Override
                    public void pick( PoseResource poseResource )
                    {
                        FramedAnimationEditor.this.frames.add( new Frame( poseResource.getName(), poseResource.pose ) );
                        FramedAnimationEditor.this.rebuildFrames();
                    }
                };

                posePicker.show();
            }
        } );
        buttonBar.addChild( add );

        super.createButtons( buttonBar );
    }

    @Override
    public void createForm( GridLayout gridLayout )
    {
        FramedAnimation fa = (FramedAnimation) this.animation;
        this.chkPingPong = new CheckBox( fa.pingPong );

        gridLayout.addRow( "Ping Pong", this.chkPingPong );
    }

    @Override
    public Component createExtra()
    {
        this.frames = new ArrayList<Frame>( ( (FramedAnimation) this.animation ).getFrames() );

        this.framesContainer = new Container();
        this.framesContainer.addStyle( "form" );
        this.framesGrid = new GridLayout( this.framesContainer, 4 );

        this.framesGrid.addRow( "Pose", new Label( "Frames" ), null, null );

        this.framesContainer.setLayout( this.framesGrid );

        this.rebuildFrames();

        VerticalScroll vs = new VerticalScroll( this.framesContainer );
        vs.addStyle( "panel" );

        return vs;
    }

    private void rebuildFrames()
    {
        this.framesGrid.clear();

        int i = -1;
        for ( Frame frame : this.frames ) {
            i++;
            this.rebuildFrame( i, frame );
        }
        this.framesContainer.invalidate();
    }

    private void rebuildFrame( final int i, final Frame frame )
    {
        Component image;
        ImageComponent img = new ImageComponent( frame.getPose().getSurface() );
        image = img;
        IntegerBox delay = new IntegerBox( frame.getDelay() );

        Button up = null;
        if ( i > 0 ) {
            up = new Button( "Up" );
            up.addActionListener( new ActionListener()
            {
                @Override
                public void action()
                {
                    Frame other = FramedAnimationEditor.this.frames.get( i - 1 );
                    FramedAnimationEditor.this.frames.set( i, other );
                    FramedAnimationEditor.this.frames.set( i - 1, frame );
                    FramedAnimationEditor.this.rebuildFrames();
                }
            } );
        }

        Button delete = new Button( new ImageComponent( this.editor.rules.resources.getPose( "buttonDelete" ).getSurface() ) );
        delete.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                FramedAnimationEditor.this.frames.remove( i );
                FramedAnimationEditor.this.rebuildFrames();
            }
        } );

        delete.addStyle( "plain" );

        this.framesGrid.addRow( image, delay, up, delete );
    }

    @Override
    public boolean save()
    {
        FramedAnimation framedAnimation = (FramedAnimation) this.animation;

        framedAnimation.pingPong = this.chkPingPong.getValue();
        framedAnimation.replaceFrames( this.frames );

        return true;
    }

}
