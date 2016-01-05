/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.tools;

import java.io.File;
import java.util.HashMap;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.util.Util;

public class NewGameWizard implements Page
{

    private TextBox gameIdBox;
    private TextBox gameTitleBox;
    private IntegerBox widthBox;
    private IntegerBox heightBox;
    private Label message;
    private PickerButton<File> templatePickerButton;

    public NewGameWizard()
    {
    }

    @Override
    public String getName()
    {
        return "New Game Wizard";
    }

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

        this.gameIdBox = new TextBox("myGame");
        grid.addRow("Game ID", this.gameIdBox);

        this.gameTitleBox = new TextBox("My Game");
        grid.addRow("Game Title", this.gameTitleBox);

        this.widthBox = new IntegerBox(800);
        grid.addRow("Width", this.widthBox);

        this.heightBox = new IntegerBox(600);
        grid.addRow("Height", this.heightBox);

        this.templatePickerButton = new PickerButton<File>("Game Template", null, getGameTemplates());
        grid.addRow("Template", this.templatePickerButton);

        this.message = new Label("");
        result.addChild(this.message);

        PlainContainer buttonBar = new PlainContainer();
        result.addChild(buttonBar);
        buttonBar.addStyle("buttonBar");

        GuiButton create = new GuiButton("Create");
        buttonBar.addChild(create);

        create.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onCreate();
            }

        });

        GuiButton test = new GuiButton("Test");
        buttonBar.addChild(test);

        test.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onTest();
            }

        });

        GuiButton editor = new GuiButton("Editor");
        buttonBar.addChild(editor);

        editor.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onEditor();
            }

        });

        return result;
    }

    private HashMap<String, File> getGameTemplates()
    {
        HashMap<String, File> result = new HashMap<String, File>();

        File directory = new File(Itchy.getBaseDirectory(), "resources" + File.separator +
            "templates" + File.separator + "games");

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                result.put(file.getName(), file);
            }
        }

        return result;
    }

    private void onCreate()
    {
        try {
            String error = runWizard();

            if (error == null) {
                report("Done", false);
            } else {
                report(error, true);
            }

        } catch (Exception e) {
            report("Error : " + e.getMessage(), true);
        }
    }

    private void report( String message, boolean error )
    {
        this.message.setText(message);
        this.message.addStyle("error", error);
    }

    public File getResources()
    {
        String name = this.gameIdBox.getText();
        return new File("resources" + File.separator + name + File.separator + name + ".itchy");
    }

    private void onTest()
    {
        Resources resources = new Resources();
        try {
            resources.load(getResources());
            Game game = resources.getGame();
            game.start();

        } catch (Exception e) {
            e.printStackTrace();
            report("Error : " + e.getMessage(), true);
        }
    }

    private void onEditor()
    {
        Resources resources = new Resources();
        try {
            resources.load(getResources());
            Game game = resources.getGame();

            game.startEditor();

        } catch (Exception e) {
            e.printStackTrace();
            report("Error : " + e.getMessage(), true);
        }
    }

    private String runWizard()
        throws Exception
    {
        File templateDirectory = this.templatePickerButton.getValue();
        if (templateDirectory == null) {
            return "Pick a template";
        }

        if (!this.gameIdBox.getText().matches("\\w*")) {
            return "Game ID must only contain letters and numbers";
        }

        File destinationDirectory = new File("resources" + File.separator +
            this.gameIdBox.getText());

        if (destinationDirectory.exists()) {
            return "Already exists : " + destinationDirectory.getPath();
        }

        Util.copyDirectory(templateDirectory, destinationDirectory);

        File templateFile = new File(destinationDirectory, "resources.itchy");
        File destFile = new File(destinationDirectory, this.gameIdBox.getText() + ".itchy");

        HashMap<String, String> substitutions = new HashMap<String, String>();
        substitutions.put("NAME", this.gameIdBox.getText());
        substitutions.put("TITLE", this.gameTitleBox.getText());
        substitutions.put("WIDTH", "" + this.widthBox.getValue());
        substitutions.put("HEIGHT", "" + this.heightBox.getValue());

        Util.template(templateFile, destFile, substitutions);
        templateFile.delete();

        return null;
    }

}
