/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.util.Util;

public class ForkGame implements Page
{
    private TextBox newID;

    @Override
    public String getName()
    {
        return "Fork Game";
    }

    private PickerButton<File> gamePickerButton;

    @Override
    public Component createPage()
    {
        PlainContainer result = new PlainContainer();
        result.setLayout(new VerticalLayout());
        result.setFill(true, true);
        result.setXAlignment(0.5);

        PlainContainer main = new PlainContainer();
        result.addChild(main);
        main.setExpansion(1);
        main.setYAlignment(0.25);

        PlainContainer form = new PlainContainer();
        main.addChild(form);
        form.setType("form");

        GridLayout grid = new GridLayout(form, 2);
        form.setLayout(grid);

        this.gamePickerButton = new PickerButton<File>("Pick a Game", null, getGames());
        grid.addRow("Pick a game to copy", this.gamePickerButton);

        this.newID = new TextBox("");
        grid.addRow("Pick a new game ID", this.newID);

        PlainContainer buttonBar = new PlainContainer();
        result.addChild(buttonBar);
        buttonBar.addStyle("buttonBar");

        Button copy = new Button("Copy");
        buttonBar.addChild(copy);

        copy.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                copy();
            }

        });

        return result;
    }

    private HashMap<String, File> getGames()
    {
        HashMap<String, File> result = new HashMap<String, File>();

        File directory = Itchy.getResourcesDirectory();

        for (File dir : directory.listFiles()) {
            if (dir.isDirectory()) {
                final File resourceFile = new File(dir, dir.getName() + ".itchy");
                if (resourceFile.exists()) {

                    result.put(dir.getName(), dir);

                }
            }
        }
        return result;
    }

    private void copy()
    {
        MessageBox messageBox = new MessageBox("Copying", "This may take a few seconds.");
        messageBox.showNow();

        try {
            File fromDir = this.gamePickerButton.getValue();
            File toDir = new File(fromDir.getParentFile(), this.newID.getText());

            Util.copyDirectory(fromDir, toDir);

            File oldResource = new File(toDir, Resources.getResourceFileFromDirectory(fromDir)
                .getName());
            File newResource = Resources.getResourceFileFromDirectory(toDir);

            oldResource.renameTo(newResource);

        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            messageBox.hide();
        }

    }

}
