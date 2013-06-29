package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.CostumeResource;
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
import uk.co.nickthecoder.jame.Surface;

public abstract class CostumePicker extends Window
{
    private final Resources resources;

    private final String nullText;

    public CostumePicker( Resources resources )
    {
        this( resources, null );
    }

    public CostumePicker( Resources resources, String nullText )
    {
        super( "Pick a Costume" );
        this.nullText = nullText;
        this.resources = resources;

        Container container = new Container();
        VerticalScroll vs = new VerticalScroll( container );

        this.createCostumes( container );
        this.clientArea.addChild( vs );
    }

    private void createCostumes( Container container )
    {
        GridLayout gridLayout = new GridLayout( container, 5 );
        container.addStyle( "pickGrid" );
        container.setLayout( gridLayout );

        if ( this.nullText != null ) {
            gridLayout.addChild( this.createButton( null ) );
        }

        for ( String name : this.resources.costumeNames() ) {
            CostumeResource costumeResource = this.resources.getCostumeResource( name );

            Component component = this.createButton( costumeResource );

            gridLayout.addChild( component );
        }
        gridLayout.endRow();
    }

    private Component createButton( final CostumeResource costumeResource )
    {
        // final Pose pose = poseResource.pose;
        Container container = new Container();
        container.setLayout( new VerticalLayout() );
        container.setXAlignment( 0.5f );

        Button button;
        Surface surface = costumeResource == null ? null : costumeResource.getThumbnail();
        if ( surface == null ) {
            button = new Button( costumeResource == null ? this.nullText : costumeResource.getName() );
        } else {
            ImageComponent img = new ImageComponent( surface );
            button = new Button( img );
        }

        button.addActionListener( new ActionListener()
        {
            @Override
            public void action()
            {
                CostumePicker.this.destroy();
                CostumePicker.this.pick( costumeResource );
            }
        } );

        Label label = new Label( costumeResource == null ? this.nullText : costumeResource.getName() );

        container.addChild( button );
        container.addChild( label );

        return container;
    }

    public abstract void pick( CostumeResource costumeResource );

}
