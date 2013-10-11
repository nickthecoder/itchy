/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.wizard;

import java.io.File;
import java.util.HashMap;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.PickerButton;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.util.Util;

public class NewGameWizard extends Game
{
    private static final String RULES = "resources/editor/style.xml";

    public GuiPose mainGuiPose;

    public NewGameWizard(Resources resources)
        throws Exception
    {
        super(resources);

        try {
            setStylesheet(new Stylesheet(new File(RULES)));
        } catch (Exception e) {
            System.err.println("Failed to load stylesheet : " + RULES);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivate()
    {
        super.onActivate();

        Itchy.enableKeyboardRepeat(true);

        this.mainGuiPose = new GuiPose();
        this.mainGuiPose.setLayout(new VerticalLayout());
        this.mainGuiPose.setFill(true, true);
        this.mainGuiPose.addStyle("editor");

        this.mainGuiPose.setMinimumWidth(this.getWidth());
        this.mainGuiPose.setMinimumHeight(this.getHeight());

        this.mainGuiPose.setMaximumWidth(this.getWidth());
        this.mainGuiPose.setMaximumHeight(this.getHeight());

        this.mainGuiPose.show();

        createForm();
    }

    private TextBox gameIdBox;
    private TextBox gameTitleBox;
    private IntegerBox widthBox;
    private IntegerBox heightBox;
    private Label message;
    private PickerButton<File> templatePickerButton;

    private void createForm()
    {
        this.mainGuiPose.setLayout(new VerticalLayout());
        Container form = new Container();
        this.mainGuiPose.addChild(form);

        Container main = new Container();
        this.mainGuiPose.addChild(main);
        GridLayout grid = new GridLayout(main, 2);
        main.setLayout(grid);

        this.gameIdBox = new TextBox("myGame");
        grid.addRow("Game ID", this.gameIdBox);

        this.gameTitleBox = new TextBox("My Game");
        grid.addRow("Game Title", this.gameTitleBox);

        this.widthBox = new IntegerBox(800);
        grid.addRow("Width", this.widthBox);

        this.heightBox = new IntegerBox(600);
        grid.addRow("Height", this.heightBox);

        this.templatePickerButton = new PickerButton<File>("Game Template", null,
            getGameTemplates());
        grid.addRow("Template", this.templatePickerButton);

        this.message = new Label("");
        this.mainGuiPose.addChild(this.message);

        Container buttonBar = new Container();
        this.mainGuiPose.addChild(buttonBar);
        buttonBar.addStyle("buttonBar");

        Button create = new Button("Create");
        buttonBar.addChild(create);

        create.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onCreate();
            }

        });

        Button test = new Button("Test");
        buttonBar.addChild(test);

        test.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onTest();
            }

        });

        Button editor = new Button("Editor");
        buttonBar.addChild(editor);

        editor.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                onEditor();
            }

        });
    }

    private HashMap<String, File> getGameTemplates()
    {
        HashMap<String, File> result = new HashMap<String, File>();

        File directory = this.resources.resolveFile(
            new File(".." + File.separator + "templates" + File.separator + "games"));

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
        return new File("resources" + File.separator + name + File.separator + name + ".xml");
    }

    private void onTest()
    {
        Resources resources = new Resources();
        try {
            resources.load(getResources());
            resources.getGame().start();

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
            Editor editor = new Editor(resources.getGame());
            Itchy.startGame(editor);

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

        File destinationDirectory = this.resources.resolveFile(
            new File(".." + File.separator + this.gameIdBox.getText()));

        if (destinationDirectory.exists()) {
            return "Already exists : " + destinationDirectory.getPath();
        }

        Util.copyDirectory(templateDirectory, destinationDirectory);

        File templateFile = new File( destinationDirectory, "resources.xml" );
        File destFile = new File( destinationDirectory, this.gameIdBox.getText() + ".xml" );
        
        HashMap<String,String> substitutions = new HashMap<String,String>();
        substitutions.put("NAME", this.gameIdBox.getText() );
        substitutions.put("TITLE", this.gameTitleBox.getText() );
        substitutions.put("WIDTH", "" + this.widthBox.getValue() );
        substitutions.put("HEIGHT", "" + this.heightBox.getValue() );
        
        Util.template(templateFile, destFile, substitutions);
        templateFile.delete();
        
        return null;
    }

}