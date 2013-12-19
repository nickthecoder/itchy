/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.tools.ForkGame;
import uk.co.nickthecoder.itchy.tools.GameMenu;
import uk.co.nickthecoder.itchy.tools.NewGameWizard;
import uk.co.nickthecoder.itchy.tools.Page;

public class Launcher extends AbstractDirector
{
    private static final String RULES = "resources" + File.separator + "editor" + File.separator + "style.xml";

    public RootContainer root;

    @Override
    public void onStarted()
    {
        super.onStarted();

        System.out.println("Starting Launcher");
        Itchy.enableKeyboardRepeat(true);

        try {
            this.game.setStylesheet(new Stylesheet(new File(Itchy.getBaseDirectory(), RULES)));
        } catch (Exception e) {
            System.err.println("Failed to load stylesheet : " + RULES);
            e.printStackTrace();
        }

        System.out.println("Creating Laucher window");
        this.root = new RootContainer();
        this.root.addStyle("editor");

        this.root.setMinimumWidth(this.game.getWidth());
        this.root.setMinimumHeight(this.game.getHeight());

        this.root.setMaximumWidth(this.game.getWidth());
        this.root.setMaximumHeight(this.game.getHeight());

        createWindow();

        this.root.show();
    }

    @Override
    public void onActivate()
    {
    }

    private void createWindow()
    {
        this.root.setLayout(new VerticalLayout());
        this.root.setFill(true, true);

        Notebook notebook = new Notebook();
        notebook.setFill(true, true);

        List<Page> pages = new ArrayList<Page>();
        pages.add(new GameMenu());
        pages.add(new NewGameWizard());
        pages.add(new ForkGame());

        for (Page page : pages) {
            createNotebookPage(notebook, page);
        }

        this.root.addChild(notebook);
    }

    private void createNotebookPage( Notebook notebook, final Page page )
    {
        final Container container = new Container();
        container.addChild(page.createPage());
        container.setFill(true, true);
        notebook.addPage(page.getName(), container);
        Button button = notebook.getTab(notebook.size() - 1);
        button.addActionListener(new ActionListener() {

            @Override
            public void action()
            {
                container.clear();
                container.addChild(page.createPage());
            }

        });
    }

    public static void main( String argv[] ) throws Exception
    {
        for (String arg : argv) {
            System.out.println(arg);
        }

        String name;
        if (argv.length == 0) {
            name = "Launcher";
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
        resources.load(resourcesFile);

        Game game = resources.getGame();
        if ((argv.length == 1) && ("--editor".equals(argv[0]))) {

            game.startEditor();

        } else {
            game.start();
        }

    }

}
