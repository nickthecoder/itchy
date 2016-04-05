/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Input;
import uk.co.nickthecoder.itchy.InputInterface;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.Layout;
import uk.co.nickthecoder.itchy.PlainDirector;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;

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

    private Notebook notebook;
    
    public GameInfoEditor gameInfoEditor;

    public ListSpriteSheets listSpriteSheets;

    public ListPoses listPoses;

    public ListAnimations listAnimations;

    public ListNinePatches listNinePatches;

    public ListSounds listSounds;

    public ListFonts listFonts;

    public ListInputs listInputs;

    public ListCostumes listCostumes;

    public ListScenes listScenes;

    public ListLayouts listLayouts;

    public SceneDesigner sceneDesigner;

    
    private String designSceneName = null;

    private InputInterface inputTest = Input.parse("ctrl+t");
    
    private InputInterface inputRun = Input.parse("ctrl+r");
    
    private InputInterface inputQuit = Input.parse("ctrl+q");
    
    private InputInterface inputSave = Input.parse("ctrl+s");
    
    
    private InputInterface inputDebug1 = Input.parse("ctrl+F1"); 

    private InputInterface inputDebug2 = Input.parse("ctrl+F2"); 

    private InputInterface inputDebug3 = Input.parse("ctrl+F3"); 

    
    public Editor(Game game) throws Exception
    {
        super(game.resources);
        this.game = game;
        instance = this;
        setDirector(new PlainDirector());

        this.init();

        this.preferences = new EditorPreferences();

        this.gameInfoEditor = new GameInfoEditor(this);
        this.listSpriteSheets = new ListSpriteSheets(this.resources);
        this.listSounds = new ListSounds(this.resources);
        this.listPoses = new ListPoses(this.resources);
        this.listFonts = new ListFonts(this.resources);
        this.listNinePatches = new ListNinePatches(this.resources);
        this.listAnimations = new ListAnimations(this.resources);
        this.listCostumes = new ListCostumes(this.resources);
        this.listScenes = new ListScenes(this);
        this.listInputs = new ListInputs(this.resources);
        this.listLayouts = new ListLayouts(this.resources);
        this.layout = new Layout();
        
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

        this.root = new RootContainer();
        this.root.setLayout(new VerticalLayout());
        this.root.setFill(true, true);
        this.root.addStyle("editor");

        this.root.setMinimumWidth(this.getWidth());
        this.root.setMinimumHeight(this.getHeight());

        this.root.setMaximumWidth(this.getWidth());
        this.root.setMaximumHeight(this.getHeight());

        this.root.show();

        notebook = new Notebook();
        this.root.addChild(notebook);
        notebook.setFill(true, true);
        notebook.setExpansion(1);

        notebook.addPage(new Label("Info"), this.gameInfoEditor.createPage());
        notebook.addPage(new Label("Sprite Sheets"), this.listSpriteSheets.createPage());
        notebook.addPage(new Label("Sprites"), this.listPoses.createPage());
        notebook.addPage(new Label("Animations"), this.listAnimations.createPage());
        notebook.addPage(new Label("Nine Patches"), this.listNinePatches.createPage());
        notebook.addPage(new Label("Sounds"), this.listSounds.createPage());
        notebook.addPage(new Label("Fonts"), this.listFonts.createPage());
        notebook.addPage(new Label("Inputs"), this.listInputs.createPage());
        notebook.addPage(new Label("Costumes"), this.listCostumes.createPage());
        notebook.addPage(new Label("Layouts"), this.listLayouts.createPage());
        notebook.addPage(new Label("Scenes"), this.listScenes.createPage());
        // notebook.addPage(new Label("Preferences"), this.preferencesEditor.createPage());

        for ( int i = 0; i < notebook.size(); i ++ ) {
            Button tab = notebook.getTab(i);
            tab.setTooltip("F" + (i+1));
        }
        
        PlainContainer buttons = new PlainContainer();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(1);

        Button test = new Button(new Label("Test"));
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
        
        Button run = new Button(new Label("Run"));
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

        Button quit = new Button(new Label("Quit"));
        quit.setTooltip("ctrl+Q");
        quit.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                Editor.this.onQuit();
            }
        });
        buttons.addChild(quit);

        Button save = new Button(new Label("Save"));
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
        this.root.reStyle();

        if (this.designSceneName != null) {
            this.listScenes.addOrEdit(this.resources.getScene(this.designSceneName));
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

    private void onQuit()
    {
        Editor.instance = null;
        Editor.this.end();
        // If the editor was started directly from the command line, then end the non-running game,
        // so that Itchy will terminate in the normal way.
        // Launcher has the corresponding Itchy.startGame(game).
        if (!Itchy.getGame().isRunning()) {
            Itchy.getGame().end();
        }
    }
    
    private void onSave()
    {
        MessageBox messageBox = null;
        try {
            this.resources.save();
            this.resources.reload();
            if (this.resources.renamesPending()) {
                messageBox = new MessageBox("Renaming costumes", "This may take a little while");
                messageBox.showNow();
                this.resources.renameResourcesInScenes();
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
        if ((ke.symbol >= Keys.F1) && (ke.symbol <= Keys.F12)) {
            int tab = ke.symbol - Keys.F1;
            if (tab < notebook.size() ) {
                notebook.getTab(tab).onClick(null);
            }
        }
        
        if (inputTest.matches(ke)) {
            test();
        }
        if (inputRun.matches(ke)) {
            run();
        }
        if (inputQuit.matches(ke)) {
            this.onQuit();
        }
        if (inputSave.matches(ke)) {
            onSave();
        }
        
        if (inputDebug1.matches(ke)) {
            debug1();
        }
        
        if (inputDebug2.matches(ke)) {
            debug2();
        }
        
        if (inputDebug3.matches(ke)) {
            debug3();
        }
    }

    public void debug1()
    {
        this.resources.dump();
    }
    
    public void debug2()
    {
        
    }
    
    public void debug3()
    {
    }
    
    @Override
    public void onKeyUp(KeyboardEvent ke)
    {
        
    }

}
