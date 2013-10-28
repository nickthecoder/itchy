/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.tools;

import java.io.File;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;

public class GameMenu
{

    public GameMenu()
    {
    }

    public String getName()
    {
        return "Game Menu";
    }

    public Component createForm()
    {
        Container result = new Container();
        result.setLayout(new VerticalLayout());
        result.setFill(true, true);

        Container menu = new Container();
        menu.setLayout(new VerticalLayout());
        menu.setSpacing(10);

        Container menuScroll = new VerticalScroll(menu);
        result.addChild(menuScroll);

        File directory = new File(Itchy.getBaseDirectory(), "resources");

        for (File dir : directory.listFiles()) {
            if (dir.isDirectory()) {
                final File resourceFile = new File(dir, dir.getName() + ".itchy");
                if (resourceFile.exists()) {

                    Container two = new Container();
                    menu.addChild(two);

                    Button playButton = new Button(dir.getName());
                    two.addChild(playButton);
                    playButton.addActionListener(new ActionListener() {
                        @Override
                        public void action()
                        {
                            launchGame(resourceFile);
                        }
                    });

                    Button editButton = new Button("Editor");
                    two.addChild(editButton);
                    editButton.addActionListener(new ActionListener() {
                        @Override
                        public void action()
                        {
                            editGame(resourceFile);
                        }
                    });

                }
            }
        }

        return result;
    }

    private void launchGame( File resourceFile )
    {
        Resources resources = new Resources();
        try {
            resources.load(resourceFile);

            resources.getGame().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editGame( File resourceFile )
    {
        Resources resources = new Resources();
        try {
            resources.load(resourceFile);

            Editor editor = new Editor(resources.getGame());
            editor.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
