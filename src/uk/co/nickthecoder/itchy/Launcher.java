/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.Arrays;

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
        for (String arg : argv) {
            System.out.println(arg);
        }
        
        if ( argv.length > 0 ) {
            String name = argv[0];
            String resourcePath;
            if (new File(name).exists()) {
                resourcePath = name;
            } else {
                resourcePath = "resources" + File.separator + name + File.separator + name + ".xml";
            }
            argv = Arrays.copyOfRange(argv, 1, argv.length);
            Launcher launcher = new Launcher(resourcePath);
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
