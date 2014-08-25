/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.tools;

import java.io.File;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.jame.Surface;

public class GameMenu implements Page
{

    @Override
    public String getName()
    {
        return "Game Menu";
    }

    @Override
    public Component createPage()
    {
        PlainContainer result = new PlainContainer();
        result.setLayout(new VerticalLayout());
        result.setFill(false, true);
        result.setXAlignment(0.5);

        PlainContainer main = new PlainContainer();
        main.setFill(false, true);
        result.addChild(main);
        main.setExpansion(1);
        main.setYAlignment(0.25);

        PlainContainer menu = new PlainContainer();
        menu.setFill(true, true);
        menu.setYAlignment(0.25);
        GridLayout grid = new GridLayout(menu, 2);
        menu.setLayout(grid);
        menu.setYSpacing(10);
        menu.setXSpacing(30);

        PlainContainer menuScroll = new VerticalScroll(menu);
        menuScroll.setYAlignment(0.25);
        menuScroll.setFill(true, true);
        main.addChild(menuScroll);

        File directory = new File(Itchy.getBaseDirectory(), "resources");
        File defaultImageFile = new File(directory, "defaultGui/images/unknown32.png");

        for (File dir : directory.listFiles()) {
            if (dir.isDirectory()) {
                
                if (ignore(dir)) {
                    continue;
                }
                
                final File resourceFile = new File(dir, dir.getName() + ".itchy");
                if (resourceFile.exists()) {

                    PlainContainer combo = new PlainContainer();
                    combo.setType("comboBox");
                    combo.addStyle("combo");
                    combo.setFill(true, true);

                    Button playButton = new Button();

                    File imageFile = new File(dir, "icon32.png");
                    try {
                        Surface image = new Surface((imageFile.exists() ? imageFile : defaultImageFile).getPath());
                        ImageComponent icon = new ImageComponent(image);
                        playButton.addChild(icon);
                    } catch (Exception e) {
                        // Do nothing
                        e.printStackTrace();
                    }
                    playButton.addChild(new Label(dir.getName()));
                    playButton.setFill(true, true);
                    playButton.setXAlignment(0.5);
                    playButton.setYAlignment(0.5);
                    playButton.setXSpacing(5);
                    playButton.setExpansion(1.0);
                    playButton.addActionListener(new ActionListener() {
                        @Override
                        public void action()
                        {
                            launchGame(resourceFile);
                        }
                    });

                    Button editButton = new Button("Edit");
                    editButton.addActionListener(new ActionListener() {
                        @Override
                        public void action()
                        {
                            editGame(resourceFile);
                        }
                    });
                    combo.addChild(playButton);
                    combo.addChild(editButton);

                    // grid.addRow(playButton, editButton);
                    grid.addChild(combo);
                }
            }
        }
        grid.endRow();

        return result;
    }

    private boolean ignore(File dir)
    {
        if (dir.getName().equals("Launcher")) {
            return true;
        }
        return false;
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
            Game game = resources.getGame();

            game.startEditor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
