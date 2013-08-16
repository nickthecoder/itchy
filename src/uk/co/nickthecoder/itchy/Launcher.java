/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import uk.co.nickthecoder.jame.RGBA;

public class Launcher extends Game
{
    private ScrollableLayer mainLayer;
    
    public Launcher( String resourcesFilename ) throws Exception
    {
        super( "Itchy Game", 800, 600 );
        this.resources.load(new File(resourcesFilename));
        
        mainLayer = new ScrollableLayer("main", this.screenRect, RGBA.BLACK);
        this.layers.add( mainLayer );

    }

    @Override
    public void init()
    {
        this.mainLayer.enableMouseListener();
        this.loadScene("start");
    }
    
    public static void main( String argv[] ) throws Exception
    {
        if ( argv.length > 0 ) {
            String resources = argv[0];
            Launcher launcher = new Launcher( resources);
            launcher.runFromMain( argv );
        } else {
            System.out.println( "Usage : Launcher RESOURCES_FILE [--editor]");
        }
    }

    @Override
    public String getInitialSceneName()
    {
        return null;
    }

}
