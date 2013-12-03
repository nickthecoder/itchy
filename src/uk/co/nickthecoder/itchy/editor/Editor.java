/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;

public final class Editor extends Game
{
    public static Editor instance;

    private static final File RULES =
        new File(new File(Itchy.getResourcesDirectory(), "editor"), "style.xml");

    public EditorPreferences preferences;

    private Game game;

    public RootContainer root;

    // public PreferencesEditor preferencesEditor;

    public GameInfoEditor gameInfoEditor;

    public SoundsEditor soundsEditor;

    public PosesEditor posesEditor;

    public FontsEditor fontsEditor;

    public NinePatchEditor ninePatchEditor;

    public AnimationsEditor animationsEditor;

    public CostumesEditor costumesEditor;

    public ScenesEditor scenesEditor;

    private String designSceneName = null;

    public Editor( Game game ) throws Exception
    {
        super(game.resources);
        this.game = game;
        instance = this;
        setDirector(new PlainDirector());

        this.init();

        this.preferences = new EditorPreferences();

        this.gameInfoEditor = new GameInfoEditor(this);
        this.soundsEditor = new SoundsEditor(this);
        this.posesEditor = new PosesEditor(this);
        this.fontsEditor = new FontsEditor(this);
        this.ninePatchEditor = new NinePatchEditor(this);
        this.animationsEditor = new AnimationsEditor(this);
        this.costumesEditor = new CostumesEditor(this);
        this.scenesEditor = new ScenesEditor(this);
        // this.preferencesEditor = new PreferencesEditor(this);

        try {
            setStylesheet(new Stylesheet(RULES));
        } catch (Exception e) {
            System.err.println("Failed to load stylesheet : " + RULES);
            e.printStackTrace();
        }
    }

    public Game getGame()
    {
        return this.game;
    }

    @Override
    public void onActivate()
    {
        instance = this;
        super.onActivate();
    }
    
    @Override
    public String getTitle()
    {
        return "Itchy Editor : " + this.game.getTitle();
    }

    @Override
    public int getWidth()
    {
        return 1000;
    }

    @Override
    public int getHeight()
    {
        return 720;
    }

    @Override
    public void start( String sceneName )
    {
        this.designSceneName = sceneName;
        this.start();
    }

    @Override
    public void start()
    {
        Itchy.startGame(this);

        Itchy.enableKeyboardRepeat(true);

        this.root = new RootContainer();
        this.root.setLayout(new VerticalLayout());
        this.root.setFill(true, true);
        this.root.addStyle("editor");

        this.root.setMinimumWidth(this.getWidth());
        this.root.setMinimumHeight(this.getHeight());

        this.root.setMaximumWidth(this.getWidth());
        this.root.setMaximumHeight(this.getHeight());

        this.root.show();

        Notebook notebook = new Notebook();
        this.root.addChild(notebook);
        notebook.setFill(true, true);
        notebook.setExpansion(1);

        notebook.addPage(new Label("Info"), this.gameInfoEditor.createPage());
        notebook.addPage(new Label("Poses"), this.posesEditor.createPage());
        notebook.addPage(new Label("Animations"), this.animationsEditor.createPage());
        notebook.addPage(new Label("Nine Patches"), this.ninePatchEditor.createPage());
        notebook.addPage(new Label("Sounds"), this.soundsEditor.createPage());
        notebook.addPage(new Label("Fonts"), this.fontsEditor.createPage());
        notebook.addPage(new Label("Costumes"), this.costumesEditor.createPage());
        notebook.addPage(new Label("Scenes"), this.scenesEditor.createPage());
        // notebook.addPage(new Label("Preferences"), this.preferencesEditor.createPage());

        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(1);

        Button quit = new Button(new Label("Quit"));
        quit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                Editor.instance = null;
                Editor.this.end();
            }
        });
        buttons.addChild(quit);

        Button save = new Button(new Label("Save"));
        buttons.addChild(save);
        save.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                Editor.this.onSave();
            }
        });
        this.root.addChild(buttons);

        this.root.setPosition(0, 0, this.getWidth(), this.getHeight());
        this.root.reStyle(); // MORE needed ?

        if (this.designSceneName != null) {
            this.scenesEditor.design(this.designSceneName);
        }

        Itchy.mainLoop();
    }

    private void onSave()
    {
        MessageBox messageBox = null;
        try {
            this.resources.save();
            if (this.resources.renamesPending()) {
                messageBox = new MessageBox("Renaming costumes", "This may take a little while");
                messageBox.showNow();
            }
            if (this.resources.renamesPending()) {
                this.resources.loadSaveAllScenes();
            }

        } catch (Exception e) {
            new MessageBox("Save Failed", e).show();
            e.printStackTrace();

        } finally {
            if (messageBox != null) {
                messageBox.hide();
            }
        }
    }

}
