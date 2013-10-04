/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.Arrays;

public class Launcher extends Game
{
    public Launcher() throws Exception
    {
    }

    @Override
    public void onActivate()
    {
        super.onActivate();
        this.loadScene(getInitialSceneName());
    }

    public static void main( String argv[] ) throws Exception
    {
        for (String arg : argv) {
            System.out.println(arg);
        }

        if (argv.length > 0) {
            String name = argv[0];
            String resourcePath;
            if (new File(name).exists()) {
                resourcePath = name;
            } else {
                resourcePath = "resources" + File.separator + name + File.separator + name + ".xml";
            }
            argv = Arrays.copyOfRange(argv, 1, argv.length);

            Resources resources = new Resources();
            resources.load(new File(resourcePath));
            resources.createGame().runFromMain(argv);
        } else {
            System.out.println("Usage : Launcher RESOURCES_FILE [--editor]");
        }
    }

    @Override
    public String getInitialSceneName()
    {
        return "start";
    }

}
