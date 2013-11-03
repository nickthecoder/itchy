/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.Arrays;

import uk.co.nickthecoder.itchy.editor.Editor;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.tools.ForkGame;
import uk.co.nickthecoder.itchy.tools.GameMenu;
import uk.co.nickthecoder.itchy.tools.NewGameWizard;

public class Launcher extends Game
{
    private static final String RULES = "resources" + File.separator + "editor" + File.separator + "style.xml";

    public GuiPose mainGuiPose;

    public Launcher( GameManager gameManager )
        throws Exception
    {
        super(gameManager);

        try {
            setStylesheet(new Stylesheet(new File(Itchy.getBaseDirectory(), RULES)));
        } catch (Exception e) {
            System.err.println("Failed to load stylesheet : " + RULES);
            e.printStackTrace();
        }
    }

    @Override
    public void onActivate()
    {
        Itchy.enableKeyboardRepeat(true);

        this.mainGuiPose = new GuiPose();
        this.mainGuiPose.addStyle("editor");

        this.mainGuiPose.setMinimumWidth(this.getWidth());
        this.mainGuiPose.setMinimumHeight(this.getHeight());

        this.mainGuiPose.setMaximumWidth(this.getWidth());
        this.mainGuiPose.setMaximumHeight(this.getHeight());

        createWindow();
        this.mainGuiPose.show();

    }

    private void createWindow()
    {
        this.mainGuiPose.setLayout(new VerticalLayout());
        Container form = new Container();
        this.mainGuiPose.addChild(form);

        Notebook notebook = new Notebook();

        GameMenu gameMenu = new GameMenu();
        NewGameWizard newGameWizard = new NewGameWizard();
        ForkGame forkGame = new ForkGame();

        notebook.addPage(gameMenu.getName(), gameMenu.createForm());
        notebook.addPage(newGameWizard.getName(), newGameWizard.createForm());
        notebook.addPage(forkGame.getName(), forkGame.createForm());

        this.mainGuiPose.addChild(notebook);

    }

    public static void main( String argv[] ) throws Exception
    {
        for (String arg : argv) {
            System.out.println(arg);
        }

        String name;
        if (argv.length == 0) {
            name = "launcher";
        } else {
            name = argv[0];
            argv = Arrays.copyOfRange(argv, 1, argv.length);
        }

        File resourcesFile = new File(name);
        if (resourcesFile.exists() && (resourcesFile.isFile())) {
        } else {
            resourcesFile = new File(Itchy.getBaseDirectory(), "resources" + File.separator + name + File.separator + name + ".itchy");
        }
        System.out.println("Loading resources : " + resourcesFile);
        Resources resources = new Resources();
        resources.load( resourcesFile );

        Game game = resources.createGame();
        if ((argv.length == 1) && ("--editor".equals(argv[0]))) {

            Editor editor = new Editor(game);
            editor.start();

        } else {
            game.start();
        }

    }

}
