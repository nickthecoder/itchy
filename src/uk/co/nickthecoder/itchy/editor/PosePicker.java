package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.gui.Window;

public abstract class PosePicker extends Window
{
    private final Resources resources;

    public PosePicker( Resources resources )
    {
        super( "Pick a Pose" );
        this.resources = resources;

        Container container = new Container();
        VerticalScroll vs = new VerticalScroll( container );

        this.createPoses( container );
        this.clientArea.addChild( vs );
        this.clientArea.addStyle( "vScrolled" );
    }

    private void createPoses( Container container )
    {
        GridLayout gridLayout = new GridLayout( container, 5 );
        container.setLayout( gridLayout );
        container.addStyle( "pickGrid" );

        for ( String name : this.resources.poseNames() ) {
            PoseResource poseResource = this.resources.getPoseResource( name );

            Component component = this.createButton( poseResource );

            gridLayout.addChild( component );
        }
        gridLayout.endRow();
    }

    private Component createButton( final PoseResource poseResource )
    {
        // final Pose pose = poseResource.pose;
        Container container = new Container();

        container.setLayout( new VerticalLayout() );
        container.setXAlignment( 0.5f );

        ImageComponent img = new ImageComponent( poseResource.getThumbnail() );
        Button button = new Button( img );
        button.addStyle( "test" );
        button.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                PosePicker.this.destroy();
                PosePicker.this.pick( poseResource );
            }
        } );

        Label label = new Label( poseResource.getName() );

        container.addChild( button );
        container.addChild( label );

        return container;
    }

    public abstract void pick( PoseResource poseResource );

}
