package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
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
import uk.co.nickthecoder.jame.Rect;

public final class Editor extends Game
{

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

    public Resources resources;

    public GuiPose mainGuiPose;

    public SoundsEditor soundsEditor;

    public PosesEditor posesEditor;

    public FontsEditor fontsEditor;

    public NinePatchEditor ninePatchEditor;

    public AnimationsEditor animationsEditor;

    public CostumesEditor costumesEditor;

    public ScenesEditor scenesEditor;

    public Rules rules;

    public final Rect size = new Rect(0, 0, 1000, 750);

    public Editor( Game game ) throws Exception
    {
        Itchy.singleton.init(this);

        this.game = game;
        this.resources = game.resources; // new Resources();

        this.rules = new Rules();
        this.rules.load("resources/defaultGui/style.xml");
    }

    @Override
    public int getWidth()
    {
        return this.size.width;
    }

    @Override
    public int getHeight()
    {
        return this.size.height;
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

        this.mainGuiPose.setMinimumWidth(this.size.width);
        this.mainGuiPose.setMinimumHeight(this.size.height);

        this.mainGuiPose.setMaximumWidth(this.size.width);
        this.mainGuiPose.setMaximumHeight(this.size.height);

        this.mainGuiPose.show();

        // this.mainLayer.add( this.mainGuiPose.getActor() );

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

        this.mainGuiPose.setPosition(0, 0, this.size.width, this.size.height);
        this.mainGuiPose.reStyle(); // TODO needed ?

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
