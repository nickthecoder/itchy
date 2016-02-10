/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractRole;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.GenericCompoundView;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.RGBAView;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.SceneStub;
import uk.co.nickthecoder.itchy.ScrollableView;
import uk.co.nickthecoder.itchy.Stage;
import uk.co.nickthecoder.itchy.StageConstraint;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.itchy.ZOrderStage;
import uk.co.nickthecoder.itchy.ZOrderStageInterface;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ButtonGroup;
import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.ComponentValidator;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.CostumePicker;
import uk.co.nickthecoder.itchy.gui.FlowLayout;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.GuiView;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.LayerPickerButton;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.QuestionBox;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SimpleTableModelRow;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableListener;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.ToggleButton;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.makeup.Makeup;
import uk.co.nickthecoder.itchy.property.Property;
import uk.co.nickthecoder.itchy.role.Follower;
import uk.co.nickthecoder.itchy.role.PlainRole;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.Reversed;
import uk.co.nickthecoder.jame.RGBA;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.Surface;
import uk.co.nickthecoder.jame.event.KeyboardEvent;
import uk.co.nickthecoder.jame.event.Keys;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;
import uk.co.nickthecoder.jame.event.MouseMotionEvent;

public class SceneDesigner implements MouseListener, KeyListener
{
    private static final int MODE_SELECT = 1;
    private static final int MODE_STAMP_COSTUME = 2;
    private static final int MODE_DRAG_SCROLL = 3;
    private static final int MODE_DRAG_ACTOR = 4;
    private static final int MODE_DRAG_HANDLE = 5;

    /**
     * Used when copying and pasting
     */
    private static Actor copiedActor;

    private final Editor editor;

    private SceneStub sceneStub;

    private Scene scene;

    private GenericCompoundView<View> designViews;

    private Layer currentLayer;

    private StageView currentStageView;

    private ZOrderStage overlayStage;

    private StageView overlayView;

    private RGBAView background;

    private final Rect sceneRect;

    private RootContainer toolbox;

    private RootContainer toolbar;

    private ButtonGroup costumeButtonGroup;

    private ToggleButton selectButton;

    private Notebook toolboxNotebook;

    private PlainContainer propertiesContainer;

    private PlainContainer roleContainer;

    private PlainContainer appearanceContainer;

    private PlainContainer layersContainer;

    private PlainContainer sceneDetailsContainer;

    private PlainContainer scenePropertiesContainer;

    private PlainContainer makeupContainer;

    private int mode = MODE_SELECT;

    private int dragStartX;
    private int dragStartY;

    private Actor currentActor;
    private Actor highlightActor;
    private Costume currentCostume;

    private Actor stampActor;

    private RotateHandleRole rotateHandle;
    private HeadingHandleRole headingHandle;

    private final List<ScaleHandleRole> scaleHandles = new ArrayList<ScaleHandleRole>();
    private final List<HandleRole> handles = new ArrayList<HandleRole>();

    private HandleRole currentHandleRole;

    private Table layersTable;

    private SimpleTableModel layersTableModel;

    private ClassNameBox roleClassName;

    /**
     * Has anything changed since onSave was last called?
     */
    private boolean changed = false;

    final UndoList undoList;

    /**
     * When a text actor is the current actor, then this will be the TextBox that you enter the Actor's text. This field
     * is used to set the focus on it whenever a new text is added, and when a shortcut is used (F8).
     */
    private Component actorTextInput;

    public SceneDesigner(Editor editor, SceneStub sceneStub, Scene scene)
    {
        this.editor = editor;
        undoList = new UndoList();
        this.sceneRect = new Rect(0, 0, editor.getGame().getWidth(), editor.getGame().getHeight());
        this.sceneStub = sceneStub;
        this.scene = scene;

        costumeButtonGroup = new ButtonGroup();

        this.scene.layout.dump();
    }

    public void go()
    {
        editor.root.hide();

        background = new RGBAView();
        editor.getViews().add(background);

        createToolbar();

        Rect wholeRect = new Rect(0, 0, editor.getWidth(), editor.getHeight());
        designViews = new GenericCompoundView<View>("designViews", wholeRect);

        Rect editRect = new Rect(0, toolbar.getHeight(),
            editor.getWidth(), editor.getHeight() - toolbar.getHeight());

        editor.getViews().add(designViews);

        createToolbox();

        for (Layer layer : scene.layout.getLayersByZOrder()) {
            View view = layer.getView();
            StageView stageView = layer.getStageView();
            if (stageView != null) {
                Stage stage = stageView.getStage();
                editor.getStages().add(stage);
                currentLayer = layer;
                currentStageView = stageView;
            }
            designViews.add(view);
        }

        overlayStage = new ZOrderStage();
        editor.getStages().add(overlayStage);

        overlayView = new StageView(editRect, overlayStage);
        editor.getViews().add(overlayView);

        editor.addMouseListener(this);
        editor.addKeyListener(this);

        createPageBorder();
        createHandles();

        setMode(MODE_SELECT);

        Surface screen = Itchy.getDisplaySurface();
        resize(screen.getWidth(), screen.getHeight());
        onCenter();

        if (scene.layout.defaultLayer != null) {
            selectLayer(scene.layout.defaultLayer);
        }
    }

    private void onDone()
    {
        if (changed) {
            QuestionBox question = new QuestionBox("Save", "Do you want save?")
            {
                @Override
                public boolean onOk()
                {
                    onSave();
                    exit();
                    return true;
                }

                @Override
                public boolean onCancel()
                {
                    exit();
                    return true;
                }
            };
            question.okLabel.setText("Save");
            question.cancelLabel.setText("Discard Changes");
            question.show();

        } else {
            exit();
        }
    }

    private void exit()
    {
        editor.clear();
        editor.sceneDesigner = null;
        editor.getStages().clear();
        overlayStage.clear();

        editor.getStages().remove(overlayStage);
        editor.getViews().remove(designViews);
        editor.getViews().remove(overlayView);
        editor.getViews().remove(background);
        editor.removeMouseListener(this);
        editor.removeKeyListener(this);

        Itchy.getGame().removeKeyListener(this);

        toolbox.hide();
        toolbar.hide();

        editor.root.show();
        editor.listScenes.rebuildTable();
    }

    public void resize(int width, int height)
    {
        Rect viewsRect = new Rect(0, toolbar.getHeight(), width, height - toolbar.getHeight());
        Rect editRect = new Rect(0, 0, width, height - toolbar.getHeight());

        editor.getViews().setPosition(viewsRect);
        overlayView.setPosition(editRect);
        background.setPosition(editRect);
        designViews.setPosition(new Rect(0, 0, width, height));

        for (View view : designViews.getChildren()) {
            if ((view instanceof ScrollableView) && (view instanceof StageView)) {
                view.setPosition(editRect);
            }
        }

        Rect old = toolbox.getView().getPosition();
        Rect rect = new Rect(old.x, height - old.height, width, old.height);
        toolbox.getView().setPosition(rect);
        toolbar.getView().setPosition(
            new Rect(0, 0, width, toolbar.getHeight()));

        toolbar.setPosition(0, 0, width, toolbar.getHeight());
        toolbox.setPosition(0, 0, width, toolbox.getHeight());

        debugViews(this.editor.getGlassView().getParent(), "");
    }

    protected void debugViews(View view, String prefix)
    {
        System.out.print(prefix);
        if (view instanceof GenericCompoundView) {
            GenericCompoundView<?> gcv = (GenericCompoundView<?>) view;
            System.out.print(gcv.name + " : ");
        } else {
            System.out.print(view.getClass().getName() + " : ");
        }
        System.out.print(view.getPosition());
        if (view instanceof StageView) {
            Stage stage = ((StageView) view).getStage();
            System.out.print(" Stage " + stage + " " + stage.getActors().size() + " actors.");
        }
        System.out.println();
        if (view instanceof GenericCompoundView) {
            GenericCompoundView<?> parent = (GenericCompoundView<?>) view;
            for (View child : parent.getChildren()) {
                debugViews(child, prefix + "    ");
            }
        }
    }

    private void createPageBorder()
    {
        int margin = 0;
        NinePatch ninePatch = editor.getStylesheet().resources.getNinePatch("pageBorder");
        Surface newSurface = ninePatch.createSurface(
            sceneRect.width + margin * 2,
            sceneRect.height + margin * 2);

        ImagePose newPose = new ImagePose(newSurface);
        newPose.setOffsetX(margin);
        newPose.setOffsetY(margin);

        Actor actor = new Actor(newPose);
        Role role = new PlainRole();
        actor.setRole(role);
        overlayStage.addTop(actor);
        actor.moveTo(margin, sceneRect.height - margin);

    }

    private void createToolbox()
    {
        toolbox = new RootContainer();

        toolbox = new RootContainer();
        toolbox.addStyle("toolbox");
        toolbox.draggable = true;

        toolbox.setStylesheet(editor.getStylesheet());
        toolbox.reStyle();
        toolbox.forceLayout();
        toolbox.setPosition(0, 0, editor.getWidth(), 200);
        toolbox.addStyle("semi");

        PlainContainer costumes = new PlainContainer();
        costumes.addStyle("costumes");
        costumes.setLayout(new FlowLayout());

        for (String name : editor.resources.costumeNames()) {
            Costume costume = editor.resources.getCostume(name);
            if (costume.showInDesigner) {
                this.addCostumeButton(costumes, costume);
            }
        }
        VerticalScroll costumesScroll = new VerticalScroll(costumes);

        propertiesContainer = new PlainContainer();
        VerticalScroll propertiesScroll = new VerticalScroll(propertiesContainer);

        appearanceContainer = new PlainContainer();
        VerticalScroll appearanceScroll = new VerticalScroll(appearanceContainer);

        roleContainer = new PlainContainer();
        VerticalScroll roleScroll = new VerticalScroll(roleContainer);

        layersContainer = new PlainContainer();
        VerticalScroll layersScroll = new VerticalScroll(layersContainer);

        makeupContainer = new PlainContainer();
        VerticalScroll makeupScroll = new VerticalScroll(makeupContainer);

        sceneDetailsContainer = new PlainContainer();
        scenePropertiesContainer = new PlainContainer();
        this.createScenePage();
        this.createSceneDirectorProperties();
        PlainContainer sceneDetails1 = new PlainContainer();
        sceneDetails1.setLayout(new VerticalLayout());
        sceneDetails1.addChild(sceneDetailsContainer);
        sceneDetails1.addChild(scenePropertiesContainer);
        VerticalScroll sceneDetailsScroll = new VerticalScroll(sceneDetails1);

        createLayersTable();

        toolboxNotebook = new Notebook();
        toolboxNotebook.addPage(new Label("Scene"), sceneDetailsScroll).setTooltip("ctrl+1");
        toolboxNotebook.addPage(new Label("Costumes"), costumesScroll).setTooltip("ctrl+2");
        toolboxNotebook.addPage(new Label("Actor"), propertiesScroll).setTooltip("ctrl+3");
        toolboxNotebook.addPage(new Label("Appearance"), appearanceScroll).setTooltip("ctrl+4");
        toolboxNotebook.addPage(new Label("Makeup"), makeupScroll).setTooltip("ctrl+5");
        toolboxNotebook.addPage(new Label("Role"), roleScroll).setTooltip("ctrl+6");
        toolboxNotebook.addPage(new Label("Layers"), layersScroll).setTooltip("ctrl+7");

        toolbox.setFill(true, true);
        toolboxNotebook.setFill(true, true);
        toolboxNotebook.setExpansion(1);
        costumes.setExpansion(1);
        costumes.setFill(true, true);
        costumesScroll.setExpansion(1);
        costumesScroll.setFill(true, true);

        toolbox.addChild(toolboxNotebook);

        int toolHeight = 200;
        toolbox.setMinimumWidth(editor.getWidth());
        toolbox.setMaximumHeight(toolHeight);
        toolbox.setMinimumHeight(toolHeight);

        Rect rect = new Rect(0, editor.getHeight() - toolHeight, editor.getWidth(), toolHeight);
        GuiView view = new GuiView(rect, toolbox);
        Itchy.getGame().show(view);
    }

    private void createToolbar()
    {
        toolbar = new RootContainer();

        toolbar.addStyle("toolbar");

        toolbar.setStylesheet(editor.getStylesheet());
        toolbar.reStyle();
        toolbar.forceLayout();

        addToolbarButtons(toolbar);

        toolbar.setMinimumWidth(editor.getWidth());
        toolbar.show();
        toolbar.setPosition(0, 0, editor.getWidth(),
            toolbar.getRequiredHeight());
    }

    public Button createButton(String name, String text)
    {
        Pose pose = editor.getStylesheet().resources.getPose("icon_" + name);
        if (pose == null) {
            return new Button(text);
        } else {
            ImageComponent image = new ImageComponent(pose.getSurface());
            return new Button(image);
        }
    }

    private void addToolbarButtons(Container toolbar)
    {

        Button exit = createButton("exit", "Exit");
        exit.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onDone();
            }
        });
        exit.setTooltip("Exit (ctrl+W)");
        toolbar.addChild(exit);

        Button save = createButton("save", "Save");
        save.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onSave();
            }
        });
        save.setTooltip("Save (ctrl+S)");
        toolbar.addChild(save);

        Button test = createButton("test", "Test");
        test.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onTest();
            }
        });
        test.setTooltip("Test (F12)");
        toolbar.addChild(test);

        Button home = createButton("center", "Center");
        home.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onCenter();
            }
        });
        home.setTooltip("Center View (ctrl+Home)");
        toolbar.addChild(home);

        Button cut = createButton("cut", "Cut");
        cut.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onCopy();
                onActorDelete();
            }
        });
        cut.setTooltip("Cut (ctrl+X)");
        toolbar.addChild(cut);

        Button copy = createButton("copy", "Copy");
        copy.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onCopy();
            }
        });
        copy.setTooltip("Copy (ctrl+C)");
        toolbar.addChild(copy);

        Button paste = createButton("paste", "Paste");
        paste.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onPaste();
            }
        });
        paste.setTooltip("Paste (ctrl+V)");
        toolbar.addChild(paste);

        Button actorUp = createButton("up", "Up");
        actorUp.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorUp();
            }
        });
        actorUp.setTooltip("Up the Z-Order (PageUp)");
        toolbar.addChild(actorUp);

        Button actorDown = createButton("down", "Down");
        actorDown.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorDown();
            }
        });
        actorDown.setTooltip("Down the Z-Order (PageDown)");
        toolbar.addChild(actorDown);

        Button actorTop = createButton("top", "Top");
        actorTop.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorTop();
            }
        });
        actorTop.setTooltip("Top of the Z-Order (Home)");
        toolbar.addChild(actorTop);

        Button actorBottom = createButton("bottom", "Bottom");
        actorBottom.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorBottom();
            }
        });
        actorBottom.setTooltip("Bottom of the Z-Order (End)");
        toolbar.addChild(actorBottom);

        Button actorUpLayer = createButton("moveUpLayer", "Up a Layer");
        actorUpLayer.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorUpStage();
            }
        });
        actorUpLayer.setTooltip("Move actor up a layer (ctrl+PageUp)");
        toolbar.addChild(actorUpLayer);

        Button actorDownLayer = createButton("moveDownLayer", "Down a Layer");
        actorDownLayer.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorDownStage();
            }
        });
        actorDownLayer.setTooltip("Move actor down a layer (ctrl+PageDown)");
        toolbar.addChild(actorDownLayer);

        Button actorUnrotate = createButton("unrotate", "Unrotate");
        actorUnrotate.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorUnrotate();
            }
        });
        actorUnrotate.setTooltip("Reset rotation (ctrl+o)");
        toolbar.addChild(actorUnrotate);

        Button actorUnscale = createButton("unscale", "Scale = 1");
        actorUnscale.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onActorUnscale();
            }
        });
        actorUnscale.setTooltip("Reset scale (ctrl+0)");
        toolbar.addChild(actorUnscale);

        Button textButton = createButton("text", "Text");
        textButton.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onText();
            }
        });
        textButton.setTooltip("Add Text");
        toolbar.addChild(textButton);

        Button resetZButton = createButton("resetZ", "Z");
        resetZButton.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                onResetZOrders();
            }
        });
        resetZButton.setTooltip("Reset Z Orders (#)");
        toolbar.addChild(resetZButton);

        layerPickerButton = new LayerPickerButton(this.scene.layout, this.currentLayer);
        layerPickerButton.addChangeListener(new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                SceneDesigner.this.selectLayer(layerPickerButton.getValue());
            }
        });
        layerPickerButton.setTooltip("Pick a Layer");
        toolbar.addChild(layerPickerButton);
    }

    private LayerPickerButton layerPickerButton;

    private SceneDesignerPropertiesForm<Scene> sceneForm;

    private ClassNameBox sceneDirectorName;

    private String oldSceneName;

    private TextBox sceneNameBox;

    private void createScenePage()
    {
        sceneForm = new SceneDesignerPropertiesForm<Scene>(
            "scene", this, scene, scene.getProperties());

        sceneForm.autoUpdate = true;
        sceneDetailsContainer.clear();
        sceneDetailsContainer.addChild(sceneForm.createForm());

        sceneNameBox = (TextBox) sceneForm.getComponent("name");

        oldSceneName = scene.getName();

        sceneForm.addValidator("name",
            new ComponentValidator()
            {
                @Override
                public boolean isValid()
                {
                    return sceneStub.isValidName(sceneNameBox.getText());
                }
            });

        sceneDirectorName = (ClassNameBox) sceneForm.getComponent("sceneDirectorClassName");
        sceneForm.addComponentChangeListener("sceneDirectorClassName", new ComponentChangeListener()
        {
            @Override
            public void changed()
            {
                ClassNameBox box = sceneDirectorName;
                boolean ok = editor.resources.checkClassName(box.getClassName());
                if (ok) {
                    scene.setSceneDirectorClassName(box.getClassName());
                    createSceneDirectorProperties();
                }
            }
        });

    }

    private SceneDesignerPropertiesForm<SceneDirector> sceneDirectorPropertiesForm;

    private void createSceneDirectorProperties()
    {
        try {
            sceneDirectorPropertiesForm = new SceneDesignerPropertiesForm<SceneDirector>(
                "sceneDirector", this, scene.getSceneDirector(), scene.getSceneDirector().getProperties());
        } catch (Exception e) {
            Itchy.handleException(e);
            return;
        }
        sceneDirectorPropertiesForm.autoUpdate = true;
        sceneForm.grid.ungroup();
        sceneDirectorPropertiesForm.grid.groupWith(sceneForm.grid);
        scenePropertiesContainer.clear();
        scenePropertiesContainer.addChild(sceneDirectorPropertiesForm.createForm());
    }

    private SceneDesignerPropertiesForm<Actor> actorPropertiesForm;

    private void createActorPage()
    {
        propertiesContainer.clear();
        actorPropertiesForm = new SceneDesignerPropertiesForm<Actor>(
            "actor", this, currentActor, currentActor.getProperties());

        actorPropertiesForm.autoUpdate = true;
        propertiesContainer.addChild(actorPropertiesForm.createForm());
    }

    private SceneDesignerPropertiesForm<Appearance> appearancePropertiesForm;

    private void createAppearancePage()
    {
        Appearance appearance = currentActor.getAppearance();
        appearancePropertiesForm = new SceneDesignerPropertiesForm<Appearance>(
            "appearance", this, appearance, appearance.getProperties());

        appearancePropertiesForm.autoUpdate = true;
        appearanceContainer.clear();

        Costume costume = currentActor.getCostume();
        PlainContainer container = new PlainContainer();
        container.setType("form");
        GridLayout grid = new GridLayout(container, 2);
        grid.groupWith(appearancePropertiesForm.grid);
        final Label label = new Label((costume == null) ? "None" : editor.resources.getCostumeName(costume));
        final Button button = new Button(label);
        grid.addRow("Costume", button);
        button.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                CostumePicker picker = new CostumePicker(
                    editor.resources, "none")
                {
                    @Override
                    public void pick(Costume costume)
                    {
                        if (costume == null) {
                            if (currentActor.getAppearance().getPose() instanceof ImagePose) {
                                return; // Image poses must use a costume (only text poses don't need a costume)
                            }
                            label.setText("None");
                            currentActor.setCostume(null);

                        } else {
                            label.setText(costume.getName());
                            currentActor.setCostume(costume);
                            Pose pose = costume.getPose(currentActor.getStartEvent());

                            if (pose != null) {
                                currentActor.getAppearance().setPose(pose);
                            }
                        }
                    }
                };
                picker.show();
            }
        });

        Component theRest = appearancePropertiesForm.createForm();

        appearanceContainer.setLayout(new VerticalLayout());
        appearanceContainer.addChild(container);
        appearanceContainer.addChild(theRest);

        actorTextInput = appearancePropertiesForm.getComponent("pose.text");
    }

    private void createRolePage()
    {
        SceneDesignerRole sdb = (SceneDesignerRole) currentActor.getRole();

        roleClassName = new ClassNameBox(
            editor.getScriptManager(), sdb.actualRole.getClassName(), Role.class);

        roleClassName.addChangeListener(new ComponentChangeListener()
        {

            @Override
            public void changed()
            {
                ClassName className = roleClassName.getClassName();
                SceneDesignerRole sdb = (SceneDesignerRole) currentActor.getRole();
                try {
                    Role actualRole = (Role) className.createInstance(editor.resources);
                    sdb.actualRole = actualRole;
                    SceneDesigner.this.createRoleProperties();
                    editor.resources.checkClassName(className);
                    roleClassName.removeStyle("error");

                } catch (Exception e) {
                    roleClassName.addStyle("error");
                }
            }
        });

        createRoleProperties();
    }

    private SceneDesignerPropertiesForm<Role> rolePropertiesForm;

    private void createRoleProperties()
    {
        roleClassName.remove();

        Role role = ((SceneDesignerRole) currentActor.getRole()).actualRole;
        rolePropertiesForm = new SceneDesignerPropertiesForm<Role>("role", this, role, role.getProperties());
        rolePropertiesForm.autoUpdate = true;
        roleContainer.clear();
        rolePropertiesForm.grid.addRow("Role", roleClassName);

        roleContainer.addChild(rolePropertiesForm.createForm());
    }

    private ClassNameBox makeupClassName;

    private void createMakeupPage()
    {
        Makeup makeup = currentActor.getAppearance().getMakeup();

        makeupClassName = new ClassNameBox(
            editor.getScriptManager(), Appearance.getMakeupClassName(makeup), Makeup.class);

        makeupClassName.addChangeListener(new ComponentChangeListener()
        {

            @Override
            public void changed()
            {
                ClassName className = makeupClassName
                    .getClassName();

                boolean ok = editor.resources
                    .checkClassName(className);
                if (ok) {
                    try {
                        currentActor.getAppearance().setMakeup(className);

                        SceneDesigner.this.createMakeupProperties();
                        makeupClassName.removeStyle("error");

                    } catch (Exception e) {
                        makeupClassName.addStyle("error");
                    }
                }
            }
        });

        createMakeupProperties();
    }

    private SceneDesignerPropertiesForm<Makeup> makeupPropertiesForm;

    private void createMakeupProperties()
    {
        Makeup makeup = currentActor.getAppearance().getMakeup();
        makeupClassName.remove();
        makeupPropertiesForm = new SceneDesignerPropertiesForm<Makeup>(
            "makeup", this, makeup, makeup.getProperties());

        makeupPropertiesForm.autoUpdate = true;
        makeupContainer.clear();
        makeupPropertiesForm.grid.addRow("Makeup", makeupClassName);
        makeupContainer.addChild(makeupPropertiesForm.createForm());
    }

    private void updateProperties()
    {
        if (currentActor == null) {
            propertiesContainer.clear();
            appearanceContainer.clear();
            roleContainer.clear();
            actorTextInput = null;
            makeupContainer.clear();

            actorPropertiesForm = null;
            appearancePropertiesForm = null;
            rolePropertiesForm = null;
            makeupPropertiesForm = null;

        } else {
            createActorPage();
            createRolePage();
            createAppearancePage();
            createMakeupPage();
            updateLayersTable();
        }
    }

    private void createLayersTable()
    {
        layersTableModel = new SimpleTableModel();
        for (Layer layer : scene.layout.getLayersByZOrder()) {
            StageView stageView = layer.getStageView();

            int minimumAlpha = 0;
            if (stageView != null) {
                minimumAlpha = stageView.minimumAlpha;
            }

            SimpleTableModelRow row = new SimpleTableModelRow();
            row.add(layer);
            row.add(layer.name);
            row.add(minimumAlpha);
            layersTableModel.addRow(row);
        }

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>(1);

        TableModelColumn showHideColumn = new TableModelColumn("Dim", 0, 70)
        {
            public void addPlainCell(Container container, final TableModelRow row)
            {
                final Layer layer = (Layer) row.getData(0);
                final CheckBox dim = new CheckBox(false);
                dim.addChangeListener(new ComponentChangeListener()
                {
                    @Override
                    public void changed()
                    {
                        StageView view = layer.getStageView();
                        if (view != null) {
                            view.maximumAlpha = dim.getValue() ? 80 : 255;
                        }
                    }
                });
                container.addChild(dim);
            }

            @Override
            public AbstractComponent createCell(TableModelRow row)
            {
                PlainContainer container = new PlainContainer();
                addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent(Component component, TableModelRow row)
            {
                Container container = (Container) component;
                container.clear();
                addPlainCell(container, row);
            };
        };
        columns.add(showHideColumn);

        TableModelColumn nameColumn = new TableModelColumn("Layer", 1, 300);
        columns.add(nameColumn);

        TableModelColumn minAlphaColumn = new TableModelColumn("Reveal", 2, 100)
        {
            public void addPlainCell(Container container,
                final TableModelRow row)
            {
                final Layer layer = (Layer) row.getData(0);
                final StageView stageView = layer.getStageView();
                if (stageView == null) {
                    // Do nothing - add no components.
                } else {
                    final CheckBox check = new CheckBox(stageView.minimumAlpha > 0);
                    check.addChangeListener(new ComponentChangeListener()
                    {
                        @Override
                        public void changed()
                        {
                            try {
                                stageView.minimumAlpha = (check.getValue() ? 200 : 0);
                            } catch (Exception e) {
                            }
                        }
                    });
                    container.addChild(check);
                }
            }

            @Override
            public AbstractComponent createCell(TableModelRow row)
            {
                PlainContainer container = new PlainContainer();
                container.setXAlignment(0.5);
                addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent(Component component, TableModelRow row)
            {
                Container container = (Container) component;
                container.clear();
                addPlainCell(container, row);
            };
        };
        columns.add(minAlphaColumn);

        TableModelColumn editLayerColumn = new TableModelColumn("Edit", 0, 70)
        {

            @Override
            public AbstractComponent createCell(TableModelRow row)
            {
                final Layer layer = (Layer) row.getData(0);
                Button button = new Button("...");
                button.addActionListener(new ActionListener()
                {
                    @Override
                    public void action()
                    {
                        EditLayer editLayer = new EditLayer(editor.resources, null, scene.layout, layer, false);
                        editLayer.show();
                        editLayer.hideDetails();
                    }
                });

                return button;
            };
        };
        columns.add(editLayerColumn);

        layersTable = new Table(layersTableModel, columns);
        layersTable.setFill(true, true);
        layersTable.setExpansion(1.0);

        layersContainer.setFill(true, true);
        layersContainer.addChild(layersTable);
        layersTable.setMaximumHeight(150);

        layersTable.addTableListener(new TableListener()
        {

            @Override
            public void onRowSelected(TableRow tableRow)
            {
                selectLayer((Layer) tableRow.getTableModelRow().getData(0));
            }

            @Override
            public void onRowPicked(TableRow tableRow)
            {
            }
        });
    }

    private void selectLayer(Layer layer)
    {
        currentLayer = layer;
        if (currentLayer.getStageView() != null) {
            currentStageView = currentLayer.getStageView();
            layerPickerButton.setValue(layer);
        }
        updateLayersTable();
    }

    private void updateLayersTable()
    {
        for (int i = 0; i < layersTableModel.getRowCount(); i++) {
            SimpleTableModelRow row = (SimpleTableModelRow) layersTableModel.getRow(i);
            Layer layer = (Layer) (row.getData(0));
            if (layer == currentLayer) {
                layersTable.selectRow(row);
                break;
            }
        }
    }

    private void addCostumeButton(Container container, final Costume costume)
    {
        Surface surface = costume.getThumbnail();
        if (surface != null) {
            ImageComponent img = new ImageComponent(surface);

            ToggleButton button = new ToggleButton(img);
            button.addActionListener(new ActionListener()
            {
                @Override
                public void action()
                {
                    onSelectCostume(costume);
                }
            });
            button.setTooltip(costume.getName());
            costumeButtonGroup.add(button);
            container.addChild(button);
        }
    }

    @SuppressWarnings("rawtypes")
    public SceneDesignerPropertiesForm getForm(String formName)
    {
        if ("scene".equals(formName)) {
            return sceneForm;

        } else if ("sceneDirector".equals(formName)) {
            return sceneDirectorPropertiesForm;

        } else if ("actor".equals(formName)) {
            return actorPropertiesForm;

        } else if ("appearance".equals(formName)) {
            return appearancePropertiesForm;

        } else if ("role".equals(formName)) {
            return rolePropertiesForm;

        } else if ("makeup".equals(formName)) {
            return makeupPropertiesForm;

        }
        return null;
    }

    private void setMode(int mode)
    {
        if (mode == this.mode) {
            return;
        }

        this.mode = mode;
        deleteStampActor();

        if (this.mode == MODE_SELECT) {
            costumeButtonGroup.select(selectButton);
        }
    }

    private void onSelectCostume(Costume costume)
    {
        deleteStampActor();
        selectActor(null);
        currentCostume = costume;
        setMode(MODE_STAMP_COSTUME);
        createStampActor();
    }

    @Override
    public void onKeyDown(KeyboardEvent event)
    {

        if (event.symbol == Keys.ESCAPE) {
            onEscape();
            event.stopPropagation();
        }

        if (Itchy.isCtrlDown()) {

            int scrollAmount = Itchy.isShiftDown() ? 100 : 10;

            if (event.symbol == Keys.s) {
                onSave();
                event.stopPropagation();

            } else if (event.symbol == Keys.z) {
                if (Itchy.isShiftDown()) {
                    undoList.redo();
                } else {
                    undoList.undo();
                }
                event.stopPropagation();

            } else if (event.symbol == Keys.y) {
                undoList.redo();
                event.stopPropagation();

            } else if (event.symbol == Keys.w) {
                onDone();
                event.stopPropagation();

            } else if (event.symbol == Keys.x) {
                onCopy();
                onActorDelete();
                event.stopPropagation();

            } else if (event.symbol == Keys.c) {
                onCopy();
                event.stopPropagation();

            } else if (event.symbol == Keys.v) {
                onPaste();
                event.stopPropagation();

            } else if (event.symbol == Keys.LEFT) {
                scrollBy(-scrollAmount, 0);
                event.stopPropagation();

            } else if (event.symbol == Keys.RIGHT) {
                scrollBy(scrollAmount, 0);
                event.stopPropagation();

            } else if (event.symbol == Keys.UP) {
                scrollBy(0, scrollAmount);
                event.stopPropagation();

            } else if (event.symbol == Keys.DOWN) {
                scrollBy(0, -scrollAmount);
                event.stopPropagation();

            } else if (event.symbol == Keys.DELETE) {
                onActorDelete();
                event.stopPropagation();

            } else if ((event.symbol >= Keys.KEY_1)
                && (event.symbol <= Keys.KEY_7)) {
                toolboxNotebook.selectPage(event.symbol - Keys.KEY_1);
                event.stopPropagation();

            } else if (event.symbol == Keys.HOME) {
                onCenter();
                event.stopPropagation();

            } else if (event.symbol == Keys.PAGEUP) {
                onActorUpStage();
                event.stopPropagation();

            } else if (event.symbol == Keys.PAGEDOWN) {
                onActorDownStage();
                event.stopPropagation();

            } else if (event.symbol == Keys.o) {
                onActorUnrotate();
                event.stopPropagation();

            } else if (event.symbol == Keys.KEY_0) {
                onActorUnscale();
                event.stopPropagation();

            } else if (event.symbol == Keys.HASH) {
                onResetZOrders();
                event.stopPropagation();
            }

        } else {

            int moveAmount = Itchy.isShiftDown() ? 10 : 1;

            if (event.symbol == Keys.PAGEUP) {
                onActorUp();
                event.stopPropagation();

            } else if (event.symbol == Keys.PAGEDOWN) {
                onActorDown();
                event.stopPropagation();

            } else if (event.symbol == Keys.HOME) {
                onActorTop();
                event.stopPropagation();

            } else if (event.symbol == Keys.END) {
                onActorBottom();
                event.stopPropagation();

            } else if (event.symbol == Keys.LEFT) {
                moveActor(-moveAmount, 0);
                event.stopPropagation();

            } else if (event.symbol == Keys.RIGHT) {
                moveActor(moveAmount, 0);
                event.stopPropagation();

            } else if (event.symbol == Keys.UP) {
                moveActor(0, moveAmount);
                event.stopPropagation();

            } else if (event.symbol == Keys.DOWN) {
                moveActor(0, -moveAmount);
                event.stopPropagation();

            } else if (event.symbol == Keys.F2) {
                onEditText();
                event.stopPropagation();

            } else if (event.symbol == Keys.F8) {
                onEditText();
                event.stopPropagation();

            } else if (event.symbol == Keys.F12) {
                onTest();
                event.stopPropagation();
            }

        }
    }

    private void onEscape()
    {
        setMode(MODE_SELECT);
        selectActor(null);
    }

    private void moveActor(int dx, int dy)
    {
        if (currentActor != null) {
            double x = currentActor.getX() + dx;
            double y = currentActor.getY() + dy;

            StageConstraint sc = currentStageView.getStage().getStageConstraint();
            currentActor.moveTo(sc.constrainX(x, y), sc.constrainY(x, y));
        }
    }

    @Override
    public void onKeyUp(KeyboardEvent event)
    {
    }

    @Override
    public void onMouseDown(MouseButtonEvent event)
    {
        try {
            if (!overlayView.adjustMouse(event)) {
                return;
            }
            mouseDown(event);
            event.stopPropagation();

        } finally {
            overlayView.unadjustMouse(event);
        }
    }

    @Override
    public void onMouseUp(MouseButtonEvent event)
    {
        try {
            if (!overlayView.adjustMouse(event)) {
                return;
            }
            mouseUp(event);
            event.stopPropagation();

        } finally {
            overlayView.unadjustMouse(event);
        }
    }

    @Override
    public void onMouseMove(MouseMotionEvent event)
    {
        try {
            if (!overlayView.adjustMouse(event)) {
                return;
            }
            mouseMove(event);
            event.stopPropagation();

        } finally {
            overlayView.unadjustMouse(event);
        }
    }

    private int previousClickX;
    private int previousClickY;

    private boolean isNearClick(MouseEvent event)
    {
        boolean result = (event.x - previousClickX)
            * (event.x - previousClickX)
            + (event.y - previousClickY)
            * (event.y - previousClickY) < 10;

        previousClickX = event.x;
        previousClickY = event.y;

        return result;
    }

    public void mouseDown(MouseButtonEvent event)
    {
        boolean isNearClick = isNearClick(event);

        if ((event.button == 2) || ((event.button == 1) && Itchy.isAltDown())) {
            setMode(MODE_DRAG_SCROLL);
            beginDrag(event.x, event.y);
            event.stopPropagation();
        }

        if (event.button != 1) {
            return;
        }

        if (mode == MODE_SELECT) {

            for (HandleRole handleRole : handles) {
                Actor actor = handleRole.getActor();

                if (actor.hitting(event.x, event.y) && (actor.getAppearance().getAlpha() > 0)) {
                    beginDrag(event.x, event.y);
                    handleRole.dragStart();
                    currentHandleRole = handleRole;
                    setMode(MODE_DRAG_HANDLE);
                    this.hideHighlightActor();
                    event.stopPropagation();
                }
            }

            // Has the user repeatedly clicked to find the object BELOW the
            // currently selected one?
            // (or ABOVE, if shift is held down).
            boolean searching = isNearClick
                && Itchy.isShiftDown()
                && (currentActor != null)
                && (currentActor.hitting(event.x, event.y));

            if (Itchy.isCtrlDown()) {
                // Look at ALL stages, not only the current one.

                // for (View child : Reversed.list(designViews.getChildren())) {
                for (Layer layer : Reversed.list(scene.layout.getLayersByZOrder())) {
                    StageView stageView = layer.getStageView();
                    if (stageView != null) {
                        Stage stage = stageView.getStage();

                        for (Actor actor : Reversed.list(stage.getActors())) {

                            if (actor.hitting(event.x, event.y)) {
                                if (searching) {
                                    if (actor == currentActor) {
                                        searching = false;
                                    }
                                } else {
                                    selectLayer(layer);
                                    selectActor(actor);
                                    setMode(MODE_DRAG_ACTOR);
                                    beginDrag(event.x, event.y);
                                    event.stopPropagation();
                                }
                            }
                        }
                    }
                }

            } else {

                for (Actor actor : Reversed.list(currentStageView.getStage().getActors())) {

                    if (actor.hitting(event.x, event.y)) {
                        if (searching) {
                            if (actor == currentActor) {
                                searching = false;
                            }
                        } else {
                            selectActor(actor);
                            setMode(MODE_DRAG_ACTOR);
                            beginDrag(event.x, event.y);
                            event.stopPropagation();
                        }
                    }
                }
            }

            selectActor(null);
            event.stopPropagation();
        }

        if (mode == MODE_STAMP_COSTUME) {
            Actor actor;

            Role role;

            if (stampActor.getAppearance().getPose() instanceof TextPose) {
                actor = new Actor(stampActor.getAppearance().getPose());
                if (stampActor.getCostume() == null) {
                    role = new PlainRole();
                } else {
                    try {
                        role = (Role) stampActor.getCostume().roleClassName.createInstance(this.editor.resources);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }

            } else {
                actor = new Actor(currentCostume);
                try {
                    role = (Role) stampActor.getCostume().roleClassName.createInstance(this.editor.resources);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }

            SceneDesignerRole sdRole = new SceneDesignerRole(role);
            actor.setRole(sdRole);

            if (!stampActor.isText()) {
                setDefaultProperties(role, currentCostume);
            }

            StageConstraint sc = currentStageView.getStage().getStageConstraint();

            actor.moveTo(sc.constrainX(event.x, event.y), sc.constrainY(event.x, event.y));
            if (currentCostume != null) {
                actor.setZOrder(currentCostume.defaultZOrder);
            }
            Stage stage = currentStageView.getStage();
            stage.add(actor);

            // TO DO, When stamping an actor implements undo/red, then the
            // StageConstraint will have to implement it too.
            sc.added(actor);

            if (!Itchy.isShiftDown()) {
                setMode(MODE_SELECT);
                selectActor(actor);

                onEditText();
            }
            changed = true;

            event.stopPropagation();
        }
    }

    public void mouseUp(MouseButtonEvent event)
    {
        if ((mode == MODE_DRAG_HANDLE) || (mode == MODE_DRAG_ACTOR)) {
            updateProperties();
        }

        if (mode == MODE_DRAG_HANDLE) {
            currentHandleRole.dragEnd();
            currentHandleRole = null;
            setMode(MODE_SELECT);
            selectActor(currentActor);
        }

        if ((mode == MODE_DRAG_SCROLL) || (mode == MODE_DRAG_ACTOR)) {
            setMode(MODE_SELECT);
        }
    }

    public void mouseMove(MouseMotionEvent event)
    {
        int dx = event.x - dragStartX;
        int dy = event.y - dragStartY;

        if (mode == MODE_STAMP_COSTUME) {

            StageConstraint sc = currentStageView.getStage().getStageConstraint();

            double newX = sc.constrainX(event.x, event.y);
            double newY = sc.constrainY(event.x, event.y);

            stampActor.moveTo(newX, newY);

            event.stopPropagation();

        } else if (mode == MODE_DRAG_SCROLL) {
            scrollBy(-dx, -dy);
            event.stopPropagation();

        } else if (mode == MODE_DRAG_ACTOR) {

            StageConstraint sc = currentStageView.getStage().getStageConstraint();

            double reqX = currentActor.getX() + dx;
            double reqY = currentActor.getY() + dy;

            double newX = sc.constrainX(reqX, reqY);
            double newY = sc.constrainY(reqX, reqY);

            if ((newX != currentActor.getX()) || (newY != currentActor.getY())) {
                undoList.apply(new UndoMoveActor(currentActor, newX - currentActor.getX(),
                    newY - currentActor.getY()));

                changed = true;
                beginDrag((int) (event.x + newX - reqX),
                    (int) (event.y + newY - reqY));
            }

        } else if (mode == MODE_DRAG_HANDLE) {
            currentHandleRole.moveBy(dx, dy);
            changed = true;
            beginDrag(event.x, event.y);

        }
    }

    private void beginDrag(int x, int y)
    {
        dragStartX = x;
        dragStartY = y;
    }

    private void setDefaultProperties(Role role, Costume costume)
    {
        for (Property<Role, ?> property : role.getProperties()) {
            try {
                String stringValue = costume.getString(property.access);
                if (stringValue != null) {
                    property.setValueByString(role, stringValue);
                }
            } catch (Exception e) {
                // Do nothing
            }
        }
    }

    public void selectActor(Actor actor)
    {
        currentActor = actor;
        deleteHighlightActor();
        updateProperties();

        if (actor != null) {
            createHightlightActor();
        }
        toolbox.focus(); // Remove focus from any text boxes
    }

    public Actor getCurrentActor()
    {
        return currentActor;
    }

    private void createStampActor()
    {
        assert (stampActor == null);
        stampActor = new Actor(currentCostume);

        stampActor.moveTo(-10000, -10000); // Anywhere off screen
        stampActor.getAppearance().setAlpha(128);
        overlayStage.addTop(stampActor);
    }

    private void deleteStampActor()
    {
        if (stampActor != null) {
            overlayStage.remove(stampActor);
            stampActor = null;
        }
    }

    private void scrollBy(int dx, int dy)
    {
        for (View view : designViews.getChildren()) {
            if (view instanceof ScrollableView) {
                ((ScrollableView) view).scrollBy(dx, dy);
            } else {
                Rect rect = view.getPosition();
                rect.x -= dx;
                rect.y += dy;
                view.setPosition(rect);
            }
        }
        overlayView.scrollBy(dx, dy);
    }

    private void onCenter()
    {
        int x = 0;
        int y = sceneRect.height - (int) overlayView.getVisibleRectangle().height;
        for (View view : designViews.getChildren()) {
            if (view instanceof ScrollableView) {
                ((ScrollableView) view).scrollTo(x, y);
            }
        }
        overlayView.scrollTo(x, y);
    }

    private Stage getStageBelow(Stage stage)
    {
        Stage result = null;
        for (View view : designViews.getChildren()) {
            if (view instanceof StageView) {
                if (((StageView) view).getStage() == stage) {
                    return result;
                }
                result = ((StageView) view).getStage();
            }
        }

        return null;
    }

    private Stage getStageAbove(Stage stage)
    {
        boolean found = false;
        for (View view : designViews.getChildren()) {
            if (view instanceof StageView) {
                if (found) {
                    return ((StageView) view).getStage();
                }

                if (stage == ((StageView) view).getStage()) {
                    found = true;
                }
            }
        }

        return null;
    }

    private void onActorUp()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            Stage stage = currentActor.getStage();
            if (stage instanceof ZOrderStageInterface) {
                ((ZOrderStageInterface) (stage)).zOrderUp(currentActor);
            }
        }
    }

    private void onActorDown()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            Stage stage = currentActor.getStage();
            if (stage instanceof ZOrderStageInterface) {
                ((ZOrderStageInterface) (stage)).zOrderDown(currentActor);
            }
            updateProperties();
        }
    }

    private void onActorTop()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            Stage stage = currentActor.getStage();
            if (stage instanceof ZOrderStageInterface) {
                ((ZOrderStageInterface) (stage)).addTop(currentActor);
            }
            updateProperties();
        }
    }

    private void onActorBottom()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            Stage stage = currentActor.getStage();
            if (stage instanceof ZOrderStageInterface) {
                ((ZOrderStageInterface) (stage)).addBottom(currentActor);
            }
            updateProperties();
        }
    }

    private void onCopy()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            copiedActor = copyActor(currentActor);
        }
    }

    private void onPaste()
    {
        if (SceneDesigner.copiedActor != null) {
            Actor actor = copyActor(copiedActor);
            actor.moveBy(10, 10);
            currentStageView.getStage().add(actor);
            selectActor(actor);
        }
    }

    public Actor copyActor(Actor fromActor)
    {
        Actor toActor = null;
        
        try {
            Pose pose = fromActor.getAppearance().getPose();
            if (pose instanceof TextPose) {
                TextPose textPose = (TextPose) pose;
                TextPose newTextPose = new TextPose(
                    textPose.getText(),
                    textPose.getFont(),
                    textPose.getFontSize());
                newTextPose.setAlignment(textPose.getXAlignment(), textPose.getYAlignment());
                newTextPose.setColor( new RGBA(textPose.getColor()));
                toActor = new Actor( newTextPose );
            } else {
                toActor = new Actor(fromActor.getCostume());
            }

            Role fromRole = ((SceneDesignerRole) fromActor.getRole()).actualRole;
            Role toRole = (Role) fromRole.getClassName().createInstance(this.editor.resources);
            
            Makeup fromMakeup = fromActor.getAppearance().getMakeup();
            Makeup toMakeup = (Makeup) fromMakeup.getClassName().createInstance(this.editor.resources);
            
            toActor.setRole(new SceneDesignerRole( toRole ) ); 
            toActor.getAppearance().setMakeup( toMakeup );

            for (Property<Actor, ?> property : fromActor.getProperties()) {
                Object value = property.getValue(fromActor);
                property.setValue(toActor, value);
            }
            
            for (Property<Appearance, ?> property : fromActor.getAppearance().getProperties()) {
                Object value = property.getValue(fromActor.getAppearance());
                property.setValue(toActor.getAppearance(), value);
            }
            
            for (Property<Role, ?> property : fromRole.getProperties()) {
                Object value = property.getValue(fromRole);
                property.setValue(toRole, value);
            }
            
            for (Property<Makeup, ?> property : fromActor.getAppearance().getMakeup().getProperties()) {
                Object value = property.getValue(fromActor.getAppearance().getMakeup());
                property.setValue(toActor.getAppearance().getMakeup(), value);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toActor;
    }

    private void onActorDelete()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            currentActor.kill();
            selectActor(null);
        }
    }

    private void onActorUpStage()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            Stage otherStage = getStageAbove(currentActor.getStage());

            if (otherStage != null) {
                currentActor.getStage().remove(currentActor);
                otherStage.add(currentActor);
                updateLayersTable();
            }
        }
    }

    private void onActorDownStage()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            Stage otherStage = getStageBelow(currentActor.getStage());

            if (otherStage != null) {
                currentActor.getStage().remove(currentActor);
                otherStage.add(currentActor);
                updateLayersTable();
            }
        }
    }

    private void onActorUnrotate()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            double direction = currentActor.getAppearance().getPose()
                .getDirection();
            if (currentActor.getHeading() == currentActor
                .getAppearance().getDirection()) {
                currentActor.setHeading(direction);
            }
            currentActor.setDirection(direction);
            selectActor(currentActor);
        }
    }

    private void onResetZOrders()
    {
        for (Layer layer : this.scene.layout.getLayers()) {
            Stage stage = layer.getStage();
            if (stage != null) {
                for (Actor actor : stage.getActors()) {
                    if ((actor.getCostume() != null)
                        && (actor.getCostume().defaultZOrder != 0)) {
                        if (actor.getZOrder() != actor.getCostume().defaultZOrder) {
                            actor.setZOrder(actor.getCostume().defaultZOrder);
                        }
                    }
                }
            }
        }
    }

    private void onActorUnscale()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            currentActor.getAppearance().setScale(1);
            selectActor(currentActor);
        }
    }

    private void onEditText()
    {
        if ((mode == MODE_SELECT) && (currentActor != null)) {
            if (actorTextInput != null) {
                actorTextInput.focus();
            }
        }
    }

    private void onSave()
    {
        try {
            // Rename the scene first if needed.
            if (!scene.name.equals(oldSceneName)) {
                String newName = scene.name;
                sceneStub.setName(newName);
                editor.resources.renameScene(sceneStub);
                oldSceneName = newName;
            }

            sceneStub.save(scene);
            editor.resources.save();
            changed = false;

        } catch (Exception e) {
            e.printStackTrace();
            new MessageBox("Error", "Save failed.").show();
        }
    }

    private void onTest()
    {
        onSave();

        try {

            Resources duplicate = editor.resources.copy();
            Game game = duplicate.game;
            game.testScene(scene.name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onText()
    {
        Font font = editor.resources.getDefaultFont();
        if (font == null) {
            new MessageBox("Cannot add text without a font",
                "Exit the scene designer, and add a font to your game.")
                .show();
            return;
        }
        String text = "newText";

        if (stampActor != null) {
            stampActor.kill();
        }

        selectActor(null);
        setMode(MODE_STAMP_COSTUME);

        TextPose pose = new TextPose(text, font, 22);
        stampActor = new Actor(pose);

        stampActor.moveTo(-10000, -10000); // Anywhere off screen
        overlayStage.addTop(stampActor);

    }

    private void createHightlightActor()
    {
        Surface actorSurface = currentActor.getAppearance().getSurface();

        int margin = 10;
        NinePatch ninePatch = editor.getStylesheet().resources
            .getNinePatch("highlight");
        Surface newSurface = ninePatch.createSurface(actorSurface.getWidth() + margin * 2,
            actorSurface.getHeight() + margin * 2);

        ImagePose newPose = new ImagePose(newSurface);
        newPose.setOffsetX(currentActor.getAppearance().getOffsetX() + margin);
        newPose.setOffsetY(currentActor.getAppearance().getOffsetY() + margin);

        highlightActor = new Actor(newPose);
        highlightActor.moveTo(currentActor);
        overlayStage.addTop(highlightActor);
        highlightActor.setRole(new Follower(currentActor));

        for (ScaleHandleRole be : scaleHandles) {
            be.setTarget(currentActor);
            be.getActor().getAppearance().setAlpha(255);
        }
        rotateHandle.setTarget(currentActor);
        rotateHandle.getActor().getAppearance().setAlpha(255);

        headingHandle.setTarget(currentActor);
        headingHandle.getActor().getAppearance().setAlpha(255);

    }

    private void hideHighlightActor()
    {
        for (ScaleHandleRole be : scaleHandles) {
            be.getActor().getAppearance().setAlpha(0);
        }
        if (highlightActor != null) {
            highlightActor.getAppearance().setAlpha(0);
        }
    }

    private void deleteHighlightActor()
    {
        if (highlightActor != null) {
            highlightActor.kill();
            highlightActor = null;
        }
        for (ScaleHandleRole be : scaleHandles) {
            be.setTarget(null);
        }
        rotateHandle.setTarget(null);
        headingHandle.setTarget(null);
    }

    private void createHandles()
    {

        ImagePose rotatePose = editor.getStylesheet().resources.getPose("rotateHandle");
        Actor rotateActor = new Actor(rotatePose);
        rotateHandle = new RotateHandleRole();
        rotateActor.setRole(rotateHandle);
        rotateActor.getAppearance().setAlpha(0);
        overlayStage.addTop(rotateActor);
        handles.add(rotateHandle);

        ImagePose headingPose = editor.getStylesheet().resources.getPose("headingHandle");
        Actor headingActor = new Actor(headingPose);
        headingHandle = new HeadingHandleRole();
        headingActor.setRole(headingHandle);
        headingActor.getAppearance().setAlpha(0);
        overlayStage.addTop(headingActor);
        handles.add(headingHandle);

        ImagePose scalePose = editor.getStylesheet().resources.getPose("scaleHandle");
        for (int dx = -1; dx < 2; dx += 2) {
            for (int dy = -1; dy < 2; dy += 2) {
                Actor scaleHandle = new Actor(scalePose);
                ScaleHandleRole role = new ScaleHandleRole(dx, dy);
                scaleHandle.setRole(role);
                scaleHandle.getAppearance().setAlpha(0);

                scaleHandles.add(role);
                handles.add(role);
                overlayStage.addTop(scaleHandle);
            }
        }
        scaleHandles.get(0).opposite = scaleHandles.get(3);
        scaleHandles.get(3).opposite = scaleHandles.get(0);
        scaleHandles.get(1).opposite = scaleHandles.get(2);
        scaleHandles.get(2).opposite = scaleHandles.get(1);

    }

    abstract class HandleRole extends AbstractRole
    {
        Actor target;

        int startX;
        int startY;

        boolean dragging = false;

        public void setTarget(Actor target)
        {
            this.target = target;
            if (target == null) {
                getActor().getAppearance().setAlpha(0);
            } else {
                getActor().getAppearance().setAlpha(255);
            }
        }

        public void dragStart()
        {
            startX = (int) getActor().getX();
            startY = (int) getActor().getY();
            dragging = true;
        }

        public void dragEnd()
        {
            dragging = false;
        }

        public void moveBy(int dx, int dy)
        {
            getActor().moveBy(dx, dy);
        }
    }

    class ScaleHandleRole extends HandleRole
    {
        int cornerX;
        int cornerY;
        double startScale;
        double startTargetX;
        double startTargetY;
        ScaleHandleRole opposite;

        public ScaleHandleRole(int dx, int dy)
        {
            cornerX = dx;
            cornerY = dy;
        }

        @Override
        public void dragStart()
        {
            super.dragStart();
            if (target.getAppearance().getPose() instanceof TextPose) {
                startScale = ((TextPose) (target.getAppearance().getPose())).getFontSize();
            } else {
                startScale = target.getAppearance().getScale();
            }
            startTargetX = target.getX();
            startTargetY = target.getY();
        }

        @Override
        public void tick()
        {
            if (mode == MODE_DRAG_HANDLE) {
                return;
            }

            if (target != null) {
                double x = target.getCornerX();
                double y = target.getCornerY();
                if (cornerX > 0) {
                    x += target.getAppearance().getWidth();
                }
                if (cornerY > 0) {
                    y += target.getAppearance().getHeight();
                }
                getActor().moveTo(x, y);
            }
        }

        @Override
        public void moveBy(int dx, int dy)
        {
            assert (target != null);
            UndoScaleActor undo = new UndoScaleActor(target);
            try {

                double ratioX = (target.getX() - opposite.getActor().getX())
                    / (getActor().getX() - opposite.getActor().getX());

                double ratioY = (target.getY() - opposite.getActor().getY())
                    / (getActor().getY() - opposite.getActor().getY());

                super.moveBy(dx, dy);

                Actor other = Itchy.isCtrlDown() ? target : opposite.getActor();

                double scaleX = (other.getX() - getActor().getX()) / (other.getX() - startX);
                double scaleY = (other.getY() - getActor().getY()) / (other.getY() - startY);
                double scale = Math.min(scaleX, scaleY);

                if (!Itchy.isCtrlDown()) {
                    target.moveBy(dx * ratioX, dy * ratioY);
                }

                if (target.getAppearance().getPose() instanceof TextPose) {
                    TextPose pose = (TextPose) target.getAppearance().getPose();
                    double newFontSize = startScale * scale;
                    if (newFontSize < 5) {
                        super.moveBy(-dx, -dy);
                        return;
                    }
                    pose.setFontSize(newFontSize);
                } else {
                    double newScale = startScale * scale;
                    double width = getActor().getAppearance().getPose().getSurface().getWidth() * newScale;
                    double height = getActor().getAppearance().getPose().getSurface().getHeight() * newScale;
                    if ((width < 4) || (height < 4)) {
                        super.moveBy(-dx, -dy);
                        return;
                    }
                    target.getAppearance().setScale(startScale * scale);
                }
            } finally {
                undo.end(undoList);
            }
        }

    }

    class RotateHandleRole extends HandleRole
    {
        @Override
        public void moveBy(int dx, int dy)
        {
            if (target == null) {
                return;
            }

            UndoRotateActor undo = new UndoRotateActor(target);
            try {
                super.moveBy(dx, dy);

                double tx = getActor().getX() - target.getX();
                double ty = getActor().getY() - target.getY();

                double angleRadians = Math.atan2(ty, tx);
                double headingDiff = target.getHeading() - target.getAppearance().getDirection();
                getActor().setDirectionRadians(angleRadians);

                target.getAppearance().setDirectionRadians(angleRadians);
                target.setHeading(target.getAppearance().getDirection() + headingDiff);

            } finally {
                undo.end(undoList);
            }
        }

        @Override
        public void tick()
        {
            if (dragging) {
                return;
            }

            if (target != null) {
                getActor().moveTo(target);
                getActor().setDirection(target.getAppearance().getDirection());
                getActor().moveForwards(30);
            }
        }

    }

    class HeadingHandleRole extends HandleRole
    {
        @Override
        public void moveBy(int dx, int dy)
        {
            UndoRotateActor undo = new UndoRotateActor(target);
            try {

                super.moveBy(dx, dy);

                double tx = getActor().getX() - target.getX();
                double ty = getActor().getY() - target.getY();

                double angle = Math.atan2(ty, tx);
                getActor().getAppearance().setDirectionRadians(angle);
                target.setHeadingRadians(angle);

            } finally {
                undo.end(undoList);
            }
        }

        @Override
        public void tick()
        {
            if (dragging) {
                return;
            }

            if (target != null) {
                getActor().moveTo(target);
                getActor().setDirection(target.getHeading());
                getActor().moveForwards(60);
            }
        }

    }
}
