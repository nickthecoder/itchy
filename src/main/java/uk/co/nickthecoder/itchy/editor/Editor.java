/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyInput;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.jame.event.KeyboardEvent;

public final class Editor extends Game implements KeyListener
{
    public static Editor instance;

    private static final File RULES = new File(new File(Itchy.getResourcesDirectory(), "editor"), "style.xml");

    public EditorPreferences preferences;

    private Game game;

    private int width = 1000;
    
    private int height = 720;
    
    public RootContainer root;

    // public PreferencesEditor preferencesEditor;

    public GameInfoEditor gameInfoEditor;

    public SpriteSheetsEditor spriteSheetsEditor;

    public PosesEditor posesEditor;

    public AnimationsEditor animationsEditor;

    public ListNinePatches listNinePatches;

    public SoundsEditor soundsEditor;

    public ListFonts listFonts;

    public ListInputs listInputs;

    public CostumesEditor costumesEditor;

    public ScenesEditor scenesEditor;

    public ListLayouts listLayouts;

    public SceneDesigner sceneDesigner;

    
    private String designSceneName = null;

    private KeyInput inputTest = KeyInput.parseKeyInput("ctrl+t");
    
    private KeyInput inputRun = KeyInput.parseKeyInput("ctrl+r");
    
    private KeyInput inputQuit = KeyInput.parseKeyInput("ctrl+q");
    
    private KeyInput inputSave = KeyInput.parseKeyInput("ctrl+s");
    
    public Editor(Game game) throws Exception
    {
        super(game.resources);
        this.game = game;
        instance = this;
        setDirector(new PlainDirector());

        this.init();

        this.preferences = new EditorPreferences();

        this.gameInfoEditor = new GameInfoEditor(this);
        this.spriteSheetsEditor = new SpriteSheetsEditor(this);
        this.soundsEditor = new SoundsEditor(this);
        this.posesEditor = new PosesEditor(this);
        this.listFonts = new ListFonts(this.resources);
        this.listNinePatches = new ListNinePatches(this.resources);
        this.animationsEditor = new AnimationsEditor(this);
        this.costumesEditor = new CostumesEditor(this);
        this.scenesEditor = new ScenesEditor(this);
        this.listInputs = new ListInputs(this.resources);
        this.listLayouts = new ListLayouts(this.resources);

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
    public boolean isResizable()
    {
        return true;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public void start(String sceneName)
    {
        this.designSceneName = sceneName;
        this.start();
    }

    @Override
    public void start()
    {
        Itchy.startGame(this);

        // If the editor has been started without the game being started (i.e. directly from the
        // launcher) then we need to start the game, so that it creates its layers and views.
        // For the scene designer to copy.
        if ((this.game.getStages() == null) || (this.game.getStages().size() == 0)) {
            this.game.getDirector().onStarted();
        }

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
        notebook.addPage(new Label("Sprite Sheets"), this.spriteSheetsEditor.createPage());
        notebook.addPage(new Label("Sprites"), this.posesEditor.createPage());
        notebook.addPage(new Label("Animations"), this.animationsEditor.createPage());
        notebook.addPage(new Label("Nine Patches"), this.listNinePatches.createPage());
        notebook.addPage(new Label("Sounds"), this.soundsEditor.createPage());
        notebook.addPage(new Label("Fonts"), this.listFonts.createPage());
        notebook.addPage(new Label("Inputs"), this.listInputs.createPage());
        notebook.addPage(new Label("Costumes"), this.costumesEditor.createPage());
        notebook.addPage(new Label("Layouts"), this.listLayouts.createPage());
        notebook.addPage(new Label("Scenes"), this.scenesEditor.createPage());
        // notebook.addPage(new Label("Preferences"), this.preferencesEditor.createPage());

        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(1);

        GuiButton test = new GuiButton(new Label("Test"));
        test.setTooltip("ctrl+T");
        test.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                Editor.this.test();
            }
        });
        buttons.addChild(test);
        
        GuiButton run = new GuiButton(new Label("Run"));
        run.setTooltip("ctrl+R");
        run.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                Editor.this.run();
            }
        });
        buttons.addChild(run);

        GuiButton quit = new GuiButton(new Label("Quit"));
        quit.setTooltip("ctrl+Q");
        quit.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                Editor.instance = null;
                Editor.this.end();
            }
        });
        buttons.addChild(quit);

        GuiButton save = new GuiButton(new Label("Save"));
        save.setTooltip("ctrl+S");
        buttons.addChild(save);
        save.addActionListener(new ActionListener()
        {
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
        addKeyListener(this);
        Itchy.mainLoop();
    }
    
    
    public void run()
    {
        onSave();
        
        try {

            Resources duplicate = this.resources.copy();
            Game game = duplicate.game;
            game.testScene(game.resources.getGameInfo().initialScene);

        } catch (Exception e) {
            e.printStackTrace();
        }       
    }
    
    public void test()
    {
        onSave();
        
        try {

            Resources duplicate = this.resources.copy();
            Game game = duplicate.game;
            game.testScene(game.resources.getGameInfo().testScene);

        } catch (Exception e) {
            e.printStackTrace();
        }       
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
            e.printStackTrace();
            new MessageBox("Save Failed", e).show();

        } finally {
            if (messageBox != null) {
                messageBox.hide();
            }
        }
    }

    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);
        this.width = width;
        this.height = height;
        root.setPosition(0, 0, width, height);

        root.resizeView();
        if (sceneDesigner != null) {
            sceneDesigner.resize(width, height);
        }
        
        this.root.setMinimumWidth(width);
        this.root.setMinimumHeight(height);

        this.root.setMaximumWidth(width);
        this.root.setMaximumHeight(height);

    }

    @Override
    public void onKeyDown(KeyboardEvent ke)
    {
        if (inputTest.matches(ke)) {
            test();
        }
        if (inputRun.matches(ke)) {
            run();
        }
        if (inputQuit.matches(ke)) {
            Editor.instance = null;
            Editor.this.end();
        }
        if (inputSave.matches(ke)) {
            onSave();
        }
    }

    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
        
    }

}
