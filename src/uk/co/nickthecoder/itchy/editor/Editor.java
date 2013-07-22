/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.io.File;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.Rules;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;

public final class Editor extends Game
{
    public static Editor singleton;

    public static Container addHint( Component component, String hint )
    {
        Container container = new Container();
        container.addChild(component);

        Label label = new Label(hint);
        label.addStyle("hint");
        container.addChild(label);

        return container;
    }

    public Game game;

    public GuiPose mainGuiPose;

    public SoundsEditor soundsEditor;

    public PosesEditor posesEditor;

    public FontsEditor fontsEditor;

    public NinePatchEditor ninePatchEditor;

    public AnimationsEditor animationsEditor;

    public CostumesEditor costumesEditor;

    public ScenesEditor scenesEditor;

    public Rules rules;

    public Editor( Game game ) throws Exception
    {
        this.game = game;

        singleton = this;
        Itchy.singleton.init(this);

        this.resources = game.resources;

        this.rules = new Rules(new File("resources/defaultGui/style.xml"));
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
    public void init()
    {
        Itchy.singleton.setGuiRules(this.rules);
        Itchy.singleton.enableKeyboardRepeat(true);

        this.mainGuiPose = new GuiPose();
        this.mainGuiPose.setRules(this.rules);
        this.mainGuiPose.setLayout(new VerticalLayout());
        this.mainGuiPose.setFill(true, true);
        this.mainGuiPose.addStyle("editor");

        this.mainGuiPose.setMinimumWidth(this.getWidth());
        this.mainGuiPose.setMinimumHeight(this.getHeight());

        this.mainGuiPose.setMaximumWidth(this.getWidth());
        this.mainGuiPose.setMaximumHeight(this.getHeight());

        this.mainGuiPose.show();

        this.soundsEditor = new SoundsEditor(this);
        this.posesEditor = new PosesEditor(this);
        this.fontsEditor = new FontsEditor(this);
        this.ninePatchEditor = new NinePatchEditor(this);
        this.animationsEditor = new AnimationsEditor(this);
        this.costumesEditor = new CostumesEditor(this);
        this.scenesEditor = new ScenesEditor(this);

        Notebook notebook = new Notebook();
        this.mainGuiPose.addChild(notebook);
        notebook.setFill(true, true);
        notebook.setExpansion(1);

        notebook.addPage(new Label("Poses"), this.posesEditor.createPage());
        notebook.addPage(new Label("Animations"), this.animationsEditor.createPage());
        notebook.addPage(new Label("Nine Patches"), this.ninePatchEditor.createPage());
        notebook.addPage(new Label("Sounds"), this.soundsEditor.createPage());
        notebook.addPage(new Label("Fonts"), this.fontsEditor.createPage());
        notebook.addPage(new Label("Costumes"), this.costumesEditor.createPage());
        notebook.addPage(new Label("Scenes"), this.scenesEditor.createPage());

        Container buttons = new Container();
        buttons.addStyle("buttonBar");
        buttons.setXAlignment(1);

        Button quit = new Button(new Label("Quit"));
        quit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                Editor.singleton = null;
                Editor.this.stop();
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
        this.mainGuiPose.addChild(buttons);

        this.mainGuiPose.setPosition(0, 0, this.getWidth(), this.getHeight());
        this.mainGuiPose.reStyle(); // MORE needed ?

    }

    private void onSave()
    {
        try {
            this.resources.save();
        } catch (Exception e) {
            new MessageBox("Save Failed", e.getMessage()).show();
            e.printStackTrace();
        }
    }

}
