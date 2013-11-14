/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.AbstractBehaviour;
import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.CompoundView;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.NullBehaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneActor;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.SceneResource;
import uk.co.nickthecoder.itchy.Stage;
import uk.co.nickthecoder.itchy.StageView;
import uk.co.nickthecoder.itchy.TextPose;
import uk.co.nickthecoder.itchy.View;
import uk.co.nickthecoder.itchy.ZOrderStage;
import uk.co.nickthecoder.itchy.extras.Follower;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.ButtonGroup;
import uk.co.nickthecoder.itchy.gui.CheckBox;
import uk.co.nickthecoder.itchy.gui.ClassNameBox;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FlowLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SimpleTableModelRow;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableListener;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.ToggleButton;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.util.AbstractProperty;
import uk.co.nickthecoder.itchy.util.ClassName;
import uk.co.nickthecoder.itchy.util.NinePatch;
import uk.co.nickthecoder.itchy.util.Reversed;
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
    private static SceneActor copiedActor;

    private final Editor editor;

    private final SceneResource sceneResource;

    private Scene scene;

    private CompoundView<StageView> designViews;

    private StageView currentStageView;

    private ZOrderStage overlayStage;

    private StageView overlayView;

    private final Rect sceneRect;

    private RootContainer toolbox;

    private RootContainer toolbar;

    private ButtonGroup costumeButtonGroup;

    private ToggleButton selectButton;

    private Notebook toolboxNotebook;

    private Container propertiesContainer;

    private Container behaviourContainer;

    private Container appearanceContainer;

    private Container layersContainer;

    private Container sceneDetailsContainer;

    private Container scenePropertiesContainer;

    private int mode = MODE_SELECT;

    private int dragStartX;
    private int dragStartY;

    private Actor currentActor;
    private Actor highlightActor;
    private Costume currentCostume;

    private Actor stampActor;

    private RotateHandleBehaviour rotateHandle;
    private HeadingHandleBehaviour headingHandle;

    private final List<ScaleHandleBehaviour> scaleHandles = new ArrayList<ScaleHandleBehaviour>();
    private final List<HandleBehaviour> handles = new ArrayList<HandleBehaviour>();

    private HandleBehaviour currentHandleBehaviour;

    private Table layersTable;

    private SimpleTableModel layersTableModel;

    private ClassNameBox behaviourClassName;

    /**
     * When a text actor is the current actor, then this will be the TextBox that you enter the
     * Actor's text. This field is used to set the focus on it whenever a new text is added, and
     * when a shortcut is used (F8).
     */
    private Component actorTextInput;

    public SceneDesigner( Editor editor, SceneResource sceneResource )
    {
        this.editor = editor;
        this.sceneRect = new Rect(0, 0, editor.game.getWidth(), editor.game.getHeight());
        this.sceneResource = sceneResource;
        try {
            this.scene = this.sceneResource.getScene();
        } catch (Exception e) {
            e.printStackTrace();
            onDone();
            return;
        }

        this.costumeButtonGroup = new ButtonGroup();
    }

    public void go()
    {
        this.editor.root.hide();

        createToolbar();

        Rect wholeRect = new Rect(0, 0, this.editor.getWidth(), this.editor.getHeight());
        this.designViews = new CompoundView<StageView>(wholeRect);

        Rect editRect = new Rect(0, this.toolbar.getHeight(), this.editor.getWidth(), this.editor.getHeight() - this.toolbar.getHeight());

        this.editor.getViews().add(this.designViews);

        for (Stage stage : this.editor.game.getStages()) {
            if (!stage.isLocked()) {
                Stage designStage = stage.createDesignStage();

                this.editor.getStages().add(designStage);
                StageView view = new StageView(editRect, designStage);
                this.designViews.add(view);
                this.currentStageView = view;
            }
        }

        this.overlayStage = new ZOrderStage("overlay");
        this.overlayView = new StageView(editRect, this.overlayStage);
        this.editor.getViews().add(this.overlayView);

        this.editor.addMouseListener(this);
        this.editor.addKeyListener(this);

        this.scene.create(this.editor, true);

        createPageBorder();
        createHandles();

        createToolbox();

        setMode(MODE_SELECT);

        onCenter();        
    }

    private void onDone()
    {
        this.editor.clear();
        this.editor.getStages().clear();
        this.overlayStage.clear();
        
        this.editor.getViews().remove(this.designViews);
        this.editor.getViews().remove(this.overlayView);
        this.editor.removeMouseListener(this);
        this.editor.removeKeyListener(this);
        
        Itchy.getGame().removeKeyListener(this);

        this.toolbox.hide();
        this.toolbar.hide();

        this.editor.root.show();
    }

    private void createPageBorder()
    {
        int margin = 0;
        NinePatch ninePatch = this.editor.getStylesheet().resources.getNinePatch("pageBorder");
        Surface newSurface = ninePatch.createSurface(this.sceneRect.width + margin * 2,
            this.sceneRect.height + margin * 2);

        ImagePose newPose = new ImagePose(newSurface);
        newPose.setOffsetX(margin);
        newPose.setOffsetY(margin);

        Actor actor = new Actor(newPose);
        this.overlayStage.addTop(actor);
        actor.moveTo(margin, this.sceneRect.height - margin);

    }

    private void createToolbox()
    {
        this.toolbox = new RootContainer();

        this.toolbox = new RootContainer();
        this.toolbox.addStyle("toolbox");
        this.toolbox.draggable = true;
        this.toolbox.moveTo(0, this.editor.getHeight() - this.toolbox.getHeight());

        this.toolbox.setStylesheet(this.editor.getStylesheet());
        this.toolbox.reStyle();
        this.toolbox.forceLayout();
        this.toolbox.setPosition(0, 0, this.editor.getWidth(), 200);
        this.toolbox.addStyle("semi");

        Container costumes = new Container();
        costumes.addStyle("costumes");
        costumes.setLayout(new FlowLayout());

        for (String name : this.editor.resources.costumeNames()) {
            CostumeResource costumeResource = this.editor.resources.getCostumeResource(name);
            this.addCostumeButton(costumes, costumeResource);
        }
        VerticalScroll costumesScroll = new VerticalScroll(costumes);

        this.propertiesContainer = new Container();
        VerticalScroll propertiesScroll = new VerticalScroll(this.propertiesContainer);

        this.appearanceContainer = new Container();
        VerticalScroll appearanceScroll = new VerticalScroll(this.appearanceContainer);

        this.behaviourContainer = new Container();
        VerticalScroll behaviourScroll = new VerticalScroll(this.behaviourContainer);

        this.layersContainer = new Container();
        VerticalScroll layersScroll = new VerticalScroll(this.layersContainer);

        this.sceneDetailsContainer = new Container();
        this.scenePropertiesContainer = new Container();
        this.createScenePage();
        this.createSceneBehaviourProperties();
        Container sceneDetails1 = new Container();
        sceneDetails1.setLayout(new VerticalLayout());
        sceneDetails1.addChild(this.sceneDetailsContainer);
        sceneDetails1.addChild(this.scenePropertiesContainer);
        VerticalScroll sceneDetailsScroll = new VerticalScroll(sceneDetails1);

        createLayersTable();

        this.toolboxNotebook = new Notebook();
        this.toolboxNotebook.addPage(new Label("Scene"), sceneDetailsScroll);
        this.toolboxNotebook.addPage(new Label("Costumes"), costumesScroll);
        this.toolboxNotebook.addPage(new Label("Actor"), propertiesScroll);
        this.toolboxNotebook.addPage(new Label("Appearance"), appearanceScroll);
        this.toolboxNotebook.addPage(new Label("Behaviour"), behaviourScroll);
        this.toolboxNotebook.addPage(new Label("Layers"), layersScroll);

        this.toolbox.setFill(true, true);
        this.toolboxNotebook.setFill(true, true);
        this.toolboxNotebook.setExpansion(1);
        costumes.setExpansion(1);
        costumes.setFill(true, true);
        costumesScroll.setExpansion(1);
        costumesScroll.setFill(true, true);

        this.toolbox.addChild(this.toolboxNotebook);

        int toolHeight = 200;
        this.toolbox.setMinimumWidth(this.editor.getWidth());
        this.toolbox.setMaximumHeight(toolHeight);
        this.toolbox.setMinimumHeight(toolHeight);
        this.toolbox.show();
        this.toolbox.setPosition(0, this.editor.getHeight() - toolHeight, this.editor.getWidth(), toolHeight);
    }

    private void createToolbar()
    {
        this.toolbar = new RootContainer();

        this.toolbar.addStyle("toolbar");

        this.toolbar.setStylesheet(this.editor.getStylesheet());
        this.toolbar.reStyle();
        this.toolbar.forceLayout();

        addToolbarButtons(this.toolbar);

        this.toolbar.setMinimumWidth(this.editor.getWidth());
        this.toolbar.show();
        this.toolbar.setPosition(0, 0, this.editor.getWidth(), this.toolbar.getRequiredHeight());
    }

    public Button createButton( String name, String text )
    {
        Pose pose = this.editor.getStylesheet().resources.getPose("icon_" + name);
        if (pose == null) {
            return new Button(text);
        } else {
            ImageComponent image = new ImageComponent(pose.getSurface());
            return new Button(image);
        }
    }

    private void addToolbarButtons( Container toolbar )
    {

        Button exit = createButton("exit", "Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onDone();
            }
        });
        toolbar.addChild(exit);

        Button save = createButton("save", "Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onSave();
            }
        });
        toolbar.addChild(save);

        Button test = createButton("test", "Test");
        test.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onTest();
            }
        });
        toolbar.addChild(test);

        Button home = createButton("center", "Center");
        home.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onCenter();
            }
        });
        toolbar.addChild(home);

        Button cut = createButton("cut", "Cut");
        cut.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onCopy();
                onActorDelete();
            }
        });
        toolbar.addChild(cut);

        Button copy = createButton("copy", "Copy");
        copy.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onCopy();
            }
        });
        toolbar.addChild(copy);

        Button paste = createButton("paste", "Paste");
        paste.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onPaste();
            }
        });
        toolbar.addChild(paste);

        Button actorUp = createButton("up", "Up");
        actorUp.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorUp();
            }
        });
        toolbar.addChild(actorUp);

        Button actorDown = createButton("down", "Down");
        actorDown.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorDown();
            }
        });
        toolbar.addChild(actorDown);

        Button actorTop = createButton("top", "Top");
        actorTop.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorTop();
            }
        });
        toolbar.addChild(actorTop);

        Button actorBottom = createButton("bottom", "Bottom");
        actorBottom.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorBottom();
            }
        });
        toolbar.addChild(actorBottom);

        Button actorUpLayer = createButton("moveUpLayer", "Up a Layer");
        actorBottom.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorUpLayer();
            }
        });
        toolbar.addChild(actorUpLayer);

        Button actorDownLayer = createButton("moveDownLayer", "Down a Layer");
        actorBottom.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorDownLayer();
            }
        });
        toolbar.addChild(actorDownLayer);

        Button actorUnrotate = createButton("unrotate", "Unrotate");
        actorUnrotate.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorUnrotate();
            }
        });
        toolbar.addChild(actorUnrotate);

        Button actorUnscale = createButton("unscale", "Scale = 1");
        actorUnscale.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onActorUnscale();
            }
        });
        toolbar.addChild(actorUnscale);

        Button textButton = createButton("text", "Text");
        textButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                onText();
            }
        });
        toolbar.addChild(textButton);

    }

    private PropertiesForm<SceneResource> sceneForm;

    private ClassNameBox sceneBehaviourName;

    private void createScenePage()
    {
        this.sceneForm = new PropertiesForm<SceneResource>(this.sceneResource, SceneResource.properties);
        this.sceneForm.autoUpdate = true;
        this.sceneDetailsContainer.clear();
        this.sceneDetailsContainer.addChild(this.sceneForm.createForm());

        this.sceneBehaviourName = (ClassNameBox) this.sceneForm.getComponent("scene.sceneBehaviourName");
        this.sceneForm.addComponentChangeListener("scene.sceneBehaviourName", new ComponentChangeListener() {
            @Override
            public void changed()
            {
                ClassNameBox box = SceneDesigner.this.sceneBehaviourName;
                String value = box.getClassName().name;
                boolean ok = SceneDesigner.this.editor.game.resources.registerSceneBehaviourClassName(value);
                // TODO isn't the error style done by ClassNameBox?
                box.addStyle("error", !ok);
                if (ok) {
                    SceneDesigner.this.scene.sceneBehaviourClassName = box.getClassName();
                    try {
                        SceneDesigner.this.scene.sceneBehaviour =
                            SceneDesigner.this.scene.createSceneBehaviour(
                                SceneDesigner.this.editor.resources
                                );

                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    createSceneBehaviourProperties();
                }
            }
        });

    }

    private void createSceneBehaviourProperties()
    {
        PropertiesForm<SceneBehaviour> form = new PropertiesForm<SceneBehaviour>(this.scene.sceneBehaviour,
            this.scene.sceneBehaviour.getProperties());
        form.autoUpdate = true;
        this.sceneForm.grid.ungroup();
        form.grid.groupWith(this.sceneForm.grid);
        this.scenePropertiesContainer.clear();
        this.scenePropertiesContainer.addChild(form.createForm());
    }

    private void createActorPage()
    {
        this.propertiesContainer.clear();
        PropertiesForm<Actor> form = new PropertiesForm<Actor>(this.currentActor, this.currentActor.getProperties());
        form.autoUpdate = true;
        this.propertiesContainer.addChild(form.createForm());
    }

    private void createAppearancePage()
    {
        Appearance appearance = this.currentActor.getAppearance();
        PropertiesForm<Appearance> form = new PropertiesForm<Appearance>(appearance, appearance.getProperties());
        form.autoUpdate = true;
        this.appearanceContainer.clear();
        this.appearanceContainer.addChild(form.createForm());

        this.actorTextInput = form.getComponent("pose.text");
    }

    private void createBehaviourPage()
    {
        SceneDesignerBehaviour sdb = (SceneDesignerBehaviour) this.currentActor.getBehaviour();

        this.behaviourClassName = new ClassNameBox(
            this.editor.game.getScriptManager(),
            ClassName.getClassName(sdb.actualBehaviour),
            Behaviour.class
            );

        this.behaviourClassName.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                ClassName className = SceneDesigner.this.behaviourClassName.getClassName();
                SceneDesignerBehaviour sdb = (SceneDesignerBehaviour) SceneDesigner.this.currentActor.getBehaviour();
                try {
                    sdb.setBehaviourClassName(SceneDesigner.this.editor.resources, className);
                    SceneDesigner.this.createBehaviourProperties();
                    SceneDesigner.this.editor.game.resources.registerBehaviourClassName(className.name);
                    SceneDesigner.this.behaviourClassName.removeStyle("error");

                } catch (Exception e) {
                    SceneDesigner.this.behaviourClassName.addStyle("error");
                }
            }
        });

        createBehaviourProperties();
    }

    private void createBehaviourProperties()
    {
        this.behaviourClassName.remove();

        Behaviour behaviour = ((SceneDesignerBehaviour) this.currentActor.getBehaviour()).actualBehaviour;
        PropertiesForm<Behaviour> form = new PropertiesForm<Behaviour>(behaviour, behaviour.getProperties());
        form.autoUpdate = true;
        this.behaviourContainer.clear();
        form.grid.addRow("Behaviour", this.behaviourClassName);
        this.behaviourContainer.addChild(form.createForm());
    }

    private void updateProperties()
    {
        if (this.currentActor == null) {
            this.propertiesContainer.clear();
            this.appearanceContainer.clear();
            this.behaviourContainer.clear();
            this.actorTextInput = null;
        } else {
            createActorPage();
            createBehaviourPage();
            createAppearancePage();
            updateLayersTable();
        }
    }

    private void createLayersTable()
    {
        this.layersTableModel = new SimpleTableModel();
        for (StageView stageView : this.designViews.getChildren()) {
            Stage stage = stageView.getStage();

            SimpleTableModelRow row = new SimpleTableModelRow();
            row.add(stageView);
            row.add(stage.getName());
            row.add(stageView.minimumAlpha);
            this.layersTableModel.addRow(row);
        }

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>(1);

        TableModelColumn showHideColumn = new TableModelColumn("Dim", 0, 70) {
            public void addPlainCell( Container container, final TableModelRow row )
            {
                final StageView stageView = (StageView) row.getData(0);
                final CheckBox dim = new CheckBox(false);
                dim.addChangeListener(new ComponentChangeListener() {
                    @Override
                    public void changed()
                    {
                        stageView.maximumAlpha = dim.getValue() ? 80 : 255;
                    }
                });
                container.addChild(dim);
            }

            @Override
            public Component createCell( TableModelRow row )
            {
                Container container = new Container();
                addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent( Component component, TableModelRow row )
            {
                Container container = (Container) component;
                container.clear();
                addPlainCell(container, row);
            };
        };
        columns.add(showHideColumn);

        TableModelColumn nameColumn = new TableModelColumn("Layer", 1, 300);
        columns.add(nameColumn);

        TableModelColumn minAlphaColumn = new TableModelColumn("Reveal", 2, 100) {
            public void addPlainCell( Container container, final TableModelRow row )
            {
                final StageView stageView = (StageView) row.getData(0);
                final CheckBox check = new CheckBox(stageView.minimumAlpha > 0);
                check.addChangeListener(new ComponentChangeListener() {
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

            @Override
            public Component createCell( TableModelRow row )
            {
                Container container = new Container();
                container.setXAlignment(0.5);
                addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent( Component component, TableModelRow row )
            {
                Container container = (Container) component;
                container.clear();
                addPlainCell(container, row);
            };
        };
        columns.add(minAlphaColumn);

        this.layersTable = new Table(this.layersTableModel, columns);
        this.layersTable.setFill(true, true);
        this.layersTable.setExpansion(1.0);

        this.layersContainer.setFill(true, true);
        this.layersContainer.addChild(this.layersTable);
        this.layersTable.setMaximumHeight(150);

        this.layersTable.addTableListener(new TableListener() {

            @Override
            public void onRowSelected( TableRow tableRow )
            {
                SceneDesigner.this.currentStageView = (StageView) tableRow.getTableModelRow().getData(0);
            }

            @Override
            public void onRowPicked( TableRow tableRow )
            {
            }
        });
    }

    private void updateLayersTable()
    {
        String stageName = this.currentActor == null ? "" : this.currentActor.getStage().getName();

        for (int i = 0; i < this.layersTableModel.getRowCount(); i++) {
            SimpleTableModelRow row = (SimpleTableModelRow) this.layersTableModel.getRow(i);
            String name = (String) (row.getData(1));
            if (stageName.equals(name)) {
                this.layersTable.selectRow(row);
                break;
            }
        }
    }

    private void addCostumeButton( Container container, final CostumeResource costumeResource )
    {
        Surface surface = costumeResource.getThumbnail();
        if (surface != null) {
            ImageComponent img = new ImageComponent(surface);

            ToggleButton button = new ToggleButton(img);
            button.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    onSelectCostume(costumeResource);
                }
            });
            this.costumeButtonGroup.add(button);
            container.addChild(button);
        }
    }

    private void setMode( int mode )
    {
        if (mode == this.mode) {
            return;
        }

        this.mode = mode;
        deleteStampActor();

        if (this.mode == MODE_SELECT) {
            this.costumeButtonGroup.select(this.selectButton);
        }
    }

    private void onSelectCostume( CostumeResource costumeResource )
    {
        deleteStampActor();
        selectActor(null);
        this.currentCostume = costumeResource.getCostume();
        setMode(MODE_STAMP_COSTUME);
        createStampActor();
    }

    @Override
    public boolean onKeyDown( KeyboardEvent event )
    {

        if (event.symbol == Keys.ESCAPE) {
            onEscape();
            return true;
        }

        if (Itchy.isCtrlDown()) {

            int scrollAmount = Itchy.isShiftDown() ? 100 : 10;

            if (event.symbol == Keys.s) {
                onSave();
                return true;

            } else if (event.symbol == Keys.w) {
                onDone();
                return true;

            } else if (event.symbol == Keys.x) {
                onCopy();
                onActorDelete();
                return true;

            } else if (event.symbol == Keys.c) {
                onCopy();
                return true;

            } else if (event.symbol == Keys.v) {
                onPaste();
                return true;
            } else if (event.symbol == Keys.LEFT) {
                scrollBy(-scrollAmount, 0);
                return true;

            } else if (event.symbol == Keys.RIGHT) {
                scrollBy(scrollAmount, 0);
                return true;

            } else if (event.symbol == Keys.UP) {
                scrollBy(0, scrollAmount);
                return true;

            } else if (event.symbol == Keys.DOWN) {
                scrollBy(0, -scrollAmount);
                return true;

            } else if (event.symbol == Keys.DELETE) {
                onActorDelete();
                return true;
            } else if (event.symbol == Keys.KEY_1) {
                this.toolboxNotebook.selectPage(0);
                return true;

            } else if (event.symbol == Keys.KEY_2) {
                this.toolboxNotebook.selectPage(1);
                return true;

            } else if (event.symbol == Keys.KEY_3) {
                this.toolboxNotebook.selectPage(2);
                return true;

            } else if (event.symbol == Keys.KEY_4) {
                this.toolboxNotebook.selectPage(3);
                return true;

            } else if (event.symbol == Keys.HOME) {
                onCenter();
                return true;

            } else if (event.symbol == Keys.PAGEUP) {
                onActorUpLayer();
                return true;

            } else if (event.symbol == Keys.PAGEDOWN) {
                onActorDownLayer();
                return true;

            } else if (event.symbol == Keys.o) {
                onActorUnrotate();
                return true;

            } else if (event.symbol == Keys.KEY_0) {
                onActorUnscale();
                return true;

            } else if (event.symbol == Keys.s) {
                onSave();
                return true;
            }

        } else {

            int moveAmount = Itchy.isShiftDown() ? 10 : 1;

            if (event.symbol == Keys.PAGEUP) {
                onActorUp();
                return true;

            } else if (event.symbol == Keys.PAGEDOWN) {
                onActorDown();
                return true;

            } else if (event.symbol == Keys.HOME) {
                onActorTop();
                return true;

            } else if (event.symbol == Keys.END) {
                onActorBottom();
                return true;

            } else if (event.symbol == Keys.LEFT) {
                moveActor(-moveAmount, 0);
                return true;

            } else if (event.symbol == Keys.RIGHT) {
                moveActor(moveAmount, 0);
                return true;

            } else if (event.symbol == Keys.UP) {
                moveActor(0, moveAmount);
                return true;

            } else if (event.symbol == Keys.DOWN) {
                moveActor(0, -moveAmount);
                return true;

            } else if (event.symbol == Keys.F2) {
                onEditText();
                return true;

            } else if (event.symbol == Keys.F8) {
                onEditText();
                return true;

            } else if (event.symbol == Keys.F12) {
                onTest();
                return true;
            }

        }

        return false;
    }

    private void onEscape()
    {
        setMode(MODE_SELECT);
        selectActor(null);
    }

    private void moveActor( int dx, int dy )
    {
        if (this.currentActor != null) {
            this.currentActor.moveBy(dx, dy);
        }
    }

    @Override
    public boolean onKeyUp( KeyboardEvent event )
    {
        return false;
    }

    @Override
    public boolean onMouseDown( MouseButtonEvent event )
    {
        try {
            if (!this.overlayView.adjustMouse(event)) {
                return false;
            }
            mouseDown(event);
            return true;

        } finally {
            this.overlayView.unadjustMouse(event);
        }
    }
    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        try {
            if (!this.overlayView.adjustMouse(event)) {
                return false;
            }
            mouseUp(event);
            return true;

        } finally {
            this.overlayView.unadjustMouse(event);
        }
    }
    @Override
    public boolean onMouseMove( MouseMotionEvent event )
    {
        try {
            if (!this.overlayView.adjustMouse(event)) {
                return false;
            }
            mouseMove(event);
            return true;

        } finally {
            this.overlayView.unadjustMouse(event);
        }
    }
    
    public boolean mouseDown( MouseButtonEvent event )
    {
        if ((event.button == 2) || ((event.button == 1) && Itchy.isShiftDown())) {
            setMode(MODE_DRAG_SCROLL);
            beginDrag(event);
            return true;
        }

        if (event.button != 1) {
            return false;
        }

        if (this.mode == MODE_SELECT) {

            for (HandleBehaviour handleBehaviour : this.handles) {
                Actor actor = handleBehaviour.getActor();

                if (actor.hitting(event.x, event.y)) {
                    beginDrag(event);
                    handleBehaviour.dragStart();
                    this.currentHandleBehaviour = handleBehaviour;
                    setMode(MODE_DRAG_HANDLE);
                    this.hideHighlightActor();
                    return true;
                }
            }

            boolean fromBottom = false; // Itchy.isShiftDown();

            if (Itchy.isCtrlDown()) {

                for (StageView child : fromBottom ? this.designViews.getChildren() : Reversed.list(this.designViews.getChildren())) {
                    Stage stage = child.getStage();

                    for (Iterator<Actor> i = fromBottom ?
                        stage.getActors().iterator() :
                        Reversed.list(stage.getActors()).iterator(); i.hasNext();) {

                        Actor actor = i.next();

                        if (actor.hitting(event.x, event.y)) {
                            selectActor(actor);
                            setMode(MODE_DRAG_ACTOR);
                            beginDrag(event);
                            return true;
                        }
                    }
                }

            } else {

                for (Iterator<Actor> i = fromBottom ?
                    this.currentStageView.getStage().getActors().iterator() :
                    Reversed.list(this.currentStageView.getStage().getActors()).iterator(); i.hasNext();) {

                    Actor actor = i.next();

                    if (actor.hitting(event.x, event.y)) {
                        selectActor(actor);
                        setMode(MODE_DRAG_ACTOR);
                        beginDrag(event);
                        return true;
                    }
                }
            }

            selectActor(null);
            return true;

        }

        if (this.mode == MODE_STAMP_COSTUME) {
            Actor actor;

            SceneDesignerBehaviour behaviour = new SceneDesignerBehaviour();
            ClassName behaviourClassName;

            if (this.stampActor.getAppearance().getPose() instanceof TextPose) {
                actor = new Actor(this.stampActor.getAppearance().getPose());
                if (this.stampActor.getCostume() != null) {
                    actor.setCostume(this.stampActor.getCostume());
                    behaviourClassName = this.stampActor.getCostume().behaviourClassName;
                } else {
                    behaviourClassName = new ClassName(NullBehaviour.class.getName());
                }

            } else {
                actor = new Actor(this.currentCostume);
                behaviourClassName = this.stampActor.getCostume().behaviourClassName;
            }

            try {
                behaviour.setBehaviourClassName(this.editor.resources, behaviourClassName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!this.stampActor.isText()) {
                setDefaultProperties(behaviour.actualBehaviour, this.currentCostume);
            }

            actor.moveTo(event.x, event.y);
            actor.setBehaviour(behaviour);
            if (this.currentCostume != null) {
                actor.setZOrder(this.currentCostume.defaultZOrder);
            }
            // Place on top if no default zOrder defined for the costume.
            Stage stage = this.currentStageView.getStage();
            if (actor.getZOrder() == 0) {
                if (stage instanceof ZOrderStage) {
                    ((ZOrderStage) stage).addTop(actor);
                } else {
                    stage.add(actor);
                }
            } else {
                stage.add(actor);
            }

            if (!Itchy.isShiftDown()) {
                setMode(MODE_SELECT);
                selectActor(actor);

                onEditText();
            }

            return true;
        }

        return false;
    }

    public boolean mouseUp( MouseButtonEvent event )
    {
        if ((this.mode == MODE_DRAG_HANDLE) || (this.mode == MODE_DRAG_ACTOR)) {
            updateProperties();
        }

        if (this.mode == MODE_DRAG_HANDLE) {
            this.currentHandleBehaviour.dragEnd();
            this.currentHandleBehaviour = null;
            setMode(MODE_SELECT);
            selectActor(this.currentActor);
        }

        if ((this.mode == MODE_DRAG_SCROLL) || (this.mode == MODE_DRAG_ACTOR)) {
            setMode(MODE_SELECT);
        }

        return false;
    }

    public boolean mouseMove( MouseMotionEvent event )
    {
        int dx = event.x - this.dragStartX;
        int dy = event.y - this.dragStartY;

        if (this.mode == MODE_STAMP_COSTUME) {
            this.stampActor.moveTo(event.x, event.y);

            return true;

        } else if (this.mode == MODE_DRAG_SCROLL) {
            scrollBy(-dx, -dy);
            return true;

        } else if (this.mode == MODE_DRAG_ACTOR) {
            this.currentActor.moveBy(dx, dy);
            beginDrag(event);

        } else if (this.mode == MODE_DRAG_HANDLE) {
            this.currentHandleBehaviour.moveBy(dx, dy);
            beginDrag(event);

        }

        return false;
    }

    private void beginDrag( MouseEvent event )
    {
        this.dragStartX = event.x;
        this.dragStartY = event.y;
    }

    private void setDefaultProperties( Behaviour behaviour, Costume costume )
    {
        for (AbstractProperty<Behaviour, ?> property : behaviour.getProperties()) {
            try {
                String stringValue = costume.getString(property.access);
                if (stringValue != null) {
                    property.setValueByString(behaviour, stringValue);
                }
            } catch (Exception e) {
                // Do nothing
            }
        }
    }

    private void selectActor( Actor actor )
    {
        this.currentActor = actor;
        deleteHighlightActor();
        updateProperties();

        if (actor != null) {
            createHightlightActor();
        }
    }

    private void createStampActor()
    {
        assert (this.stampActor == null);
        this.stampActor = new Actor(this.currentCostume);

        this.stampActor.moveTo(-10000, -10000); // Anywhere off screen
        this.stampActor.getAppearance().setAlpha(128);
        this.overlayStage.addTop(this.stampActor);
    }

    private void deleteStampActor()
    {
        if (this.stampActor != null) {
            this.overlayStage.remove(this.stampActor);
            this.stampActor = null;
        }
    }

    private void scrollBy( int dx, int dy )
    {
        for (StageView view : this.designViews.getChildren()) {
            view.scrollBy(dx, dy);
        }
        this.overlayView.scrollBy(dx, dy);
    }

    private void onCenter()
    {
        int x = 0;
        int y = this.sceneRect.height - (int) this.overlayView.getVisibleRectangle().height;
        for (StageView stageView : this.designViews.getChildren()) {
            stageView.scrollTo(x, y);
        }
        this.overlayView.scrollTo(x, y);
    }

    private Stage getStageBelow( Stage stage )
    {
        View result = null;
        for (View tmpStage : this.designViews.getChildren()) {
            if (stage == tmpStage) {
                return (Stage) result;
            }
            result = tmpStage;
        }

        return null;
    }

    private Stage getStageAbove( Stage stage )
    {
        boolean found = false;
        for (View tmpStage : this.designViews.getChildren()) {
            if (found) {
                return (Stage) tmpStage;
            }
            if (stage == tmpStage) {
                found = true;
            }
        }

        return null;
    }

    private void onActorUp()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.zOrderUp();
        }
    }

    private void onActorDown()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.zOrderDown();
        }
    }

    private void onActorTop()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.zOrderTop();
        }
    }

    private void onActorBottom()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.zOrderBottom();
        }
    }

    private void onCopy()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            SceneDesigner.copiedActor = SceneActor.createSceneActor(this.currentActor);
        }
    }

    private void onPaste()
    {
        if (SceneDesigner.copiedActor != null) {
            Actor actor = SceneDesigner.copiedActor.createActor(this.editor.resources, true);
            actor.moveBy(10, 10);
            addTop(this.currentStageView.getStage(), actor);
            selectActor(actor);
        }
    }

    private void onActorDelete()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.kill();
            selectActor(null);
        }
    }

    private void onActorUpLayer()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            Stage otherStage = getStageAbove(this.currentActor.getStage());

            if (otherStage != null) {
                this.currentActor.getStage().remove(this.currentActor);
                otherStage.add(this.currentActor);
                updateLayersTable();
            }
        }
    }

    private void onActorUnrotate()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            double direction = this.currentActor.getAppearance().getPose().getDirection();
            if (this.currentActor.getHeading() == this.currentActor.getAppearance().getDirection()) {
                this.currentActor.setHeading(direction);
            }
            this.currentActor.setDirection(direction);
            selectActor(this.currentActor);
        }
    }

    private void onActorUnscale()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.getAppearance().setScale(1);
            selectActor(this.currentActor);
        }
    }

    private void onActorDownLayer()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            Stage otherStage = getStageBelow(this.currentActor.getStage());

            if (otherStage != null) {
                this.currentActor.getStage().remove(this.currentActor);
                otherStage.add(this.currentActor);
                updateLayersTable();
            }
        }
    }

    private void onEditText()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            if (this.actorTextInput != null) {
                this.actorTextInput.focus();
            }
        }
    }

    private void onSave()
    {
        Scene scene = new Scene();
        scene.backgroundColor = this.scene.backgroundColor; 
        scene.showMouse = this.scene.showMouse; 
            
        scene.sceneBehaviourClassName = this.scene.sceneBehaviourClassName;
        scene.sceneBehaviour = this.scene.sceneBehaviour;

        for (StageView stageView : this.designViews.getChildren()) {

            Scene.SceneLayer sceneLayer = scene.createSceneLayer(stageView.getStage().getName());

            for (Actor actor : stageView.getStage().getActors()) {
                SceneActor sceneActor = SceneActor.createSceneActor(actor);
                sceneLayer.add(sceneActor);
            }
        }

        this.sceneResource.setScene(scene);
        try {
            this.sceneResource.save();
        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.show("Error", "Save failed.");
        }
    }

    private void onTest()
    {
        try {

            Game game = this.editor.game.resources.createGame();
            game.testScene(this.sceneResource.name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onText()
    {
        Font font = this.editor.resources.getDefaultFont();
        if (font == null) {
            return;
        }
        String text = "newText";

        if (this.stampActor != null) {
            this.stampActor.kill();
        }

        selectActor(null);
        setMode(MODE_STAMP_COSTUME);

        TextPose pose = new TextPose(text, font, 22);
        this.stampActor = new Actor(pose);

        this.stampActor.moveTo(-10000, -10000); // Anywhere off screen
        this.overlayStage.addTop(this.stampActor);

    }

    private void addTop( Stage stage, Actor actor )
    {
        if (stage instanceof ZOrderStage) {
            ((ZOrderStage) stage).addTop(actor);
        } else {
            stage.add(actor);
        }
    }

    private void createHightlightActor()
    {
        Surface actorSurface = this.currentActor.getAppearance().getSurface();

        int margin = 10;
        NinePatch ninePatch = this.editor.getStylesheet().resources.getNinePatch("highlight");
        Surface newSurface = ninePatch.createSurface(actorSurface.getWidth() + margin * 2,
            actorSurface.getHeight() + margin * 2);

        ImagePose newPose = new ImagePose(newSurface);
        newPose.setOffsetX(this.currentActor.getAppearance().getOffsetX() + margin);
        newPose.setOffsetY(this.currentActor.getAppearance().getOffsetY() + margin);

        this.highlightActor = new Actor(newPose);
        this.highlightActor.moveTo(this.currentActor);
        this.overlayStage.addTop(this.highlightActor);
        this.highlightActor.setBehaviour(new Follower(this.currentActor));

        for (ScaleHandleBehaviour be : this.scaleHandles) {
            be.setTarget(this.currentActor);
            be.getActor().getAppearance().setAlpha(255);
        }
        this.rotateHandle.setTarget(this.currentActor);
        this.rotateHandle.getActor().getAppearance().setAlpha(255);

        this.headingHandle.setTarget(this.currentActor);
        this.headingHandle.getActor().getAppearance().setAlpha(255);

    }

    private void hideHighlightActor()
    {
        for (ScaleHandleBehaviour be : this.scaleHandles) {
            be.getActor().getAppearance().setAlpha(0);
        }
        // this.rotateHandle.getActor().getAppearance().setAlpha(0);
        // this.headingHandle.getActor().getAppearance().setAlpha(0);
        if (this.highlightActor != null) {
            this.highlightActor.getAppearance().setAlpha(0);
        }
    }

    private void deleteHighlightActor()
    {
        if (this.highlightActor != null) {
            this.highlightActor.kill();
            this.highlightActor = null;
        }
        for (ScaleHandleBehaviour be : this.scaleHandles) {
            be.setTarget(null);
        }
        this.rotateHandle.setTarget(null);
        this.headingHandle.setTarget(null);
    }

    private void createHandles()
    {

        ImagePose rotatePose = this.editor.getStylesheet().resources.getPose("rotateHandle");
        Actor rotateActor = new Actor(rotatePose);
        this.rotateHandle = new RotateHandleBehaviour();
        rotateActor.setBehaviour(this.rotateHandle);
        rotateActor.getAppearance().setAlpha(0);
        this.overlayStage.addTop(rotateActor);
        this.handles.add(this.rotateHandle);

        ImagePose headingPose = this.editor.getStylesheet().resources.getPose("headingHandle");
        Actor headingActor = new Actor(headingPose);
        this.headingHandle = new HeadingHandleBehaviour();
        headingActor.setBehaviour(this.headingHandle);
        headingActor.getAppearance().setAlpha(0);
        this.overlayStage.addTop(headingActor);
        this.handles.add(this.headingHandle);

        ImagePose scalePose = this.editor.getStylesheet().resources.getPose("scaleHandle");
        for (int dx = -1; dx < 2; dx += 2) {
            for (int dy = -1; dy < 2; dy += 2) {
                Actor scaleHandle = new Actor(scalePose);
                ScaleHandleBehaviour behaviour = new ScaleHandleBehaviour(dx, dy);
                scaleHandle.setBehaviour(behaviour);
                scaleHandle.getAppearance().setAlpha(0);

                this.scaleHandles.add(behaviour);
                this.handles.add(behaviour);
                this.overlayStage.addTop(scaleHandle);
            }
        }
        this.scaleHandles.get(0).opposite = this.scaleHandles.get(3);
        this.scaleHandles.get(3).opposite = this.scaleHandles.get(0);
        this.scaleHandles.get(1).opposite = this.scaleHandles.get(2);
        this.scaleHandles.get(2).opposite = this.scaleHandles.get(1);

    }

    abstract class HandleBehaviour extends AbstractBehaviour
    {
        Actor target;

        int startX;
        int startY;

        boolean dragging = false;

        public void setTarget( Actor target )
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
            this.startX = (int) getActor().getX();
            this.startY = (int) getActor().getY();
            this.dragging = true;
        }

        public void dragEnd()
        {
            this.dragging = false;
        }

        public void moveBy( int dx, int dy )
        {
            getActor().moveBy(dx, dy);
        }
    }

    class ScaleHandleBehaviour extends HandleBehaviour
    {
        int cornerX;
        int cornerY;
        double startScale;
        double startTargetX;
        double startTargetY;
        ScaleHandleBehaviour opposite;

        public ScaleHandleBehaviour( int dx, int dy )
        {
            this.cornerX = dx;
            this.cornerY = dy;
        }

        @Override
        public void dragStart()
        {
            super.dragStart();
            if (this.target.getAppearance().getPose() instanceof TextPose) {
                this.startScale = ((TextPose) (this.target.getAppearance().getPose()))
                    .getFontSize();
            } else {
                this.startScale = this.target.getAppearance().getScale();
            }
            this.startTargetX = this.target.getX();
            this.startTargetY = this.target.getY();
        }

        @Override
        public void tick()
        {
            if (SceneDesigner.this.mode == MODE_DRAG_HANDLE) {
                return;
            }

            if (this.target != null) {
                double x = this.target.getCornerX();
                double y = this.target.getCornerY();
                if (this.cornerX > 0) {
                    x += this.target.getAppearance().getWidth();
                }
                if (this.cornerY > 0) {
                    y += this.target.getAppearance().getHeight();
                }
                getActor().moveTo(x, y);
            }
        }

        @Override
        public void moveBy( int dx, int dy )
        {
            assert (this.target != null);

            double ratioX = (this.target.getX() - this.opposite.getActor().getX()) /
                (getActor().getX() - this.opposite.getActor().getX());
            double ratioY = (this.target.getY() - this.opposite.getActor().getY()) /
                (getActor().getY() - this.opposite.getActor().getY());

            super.moveBy(dx, dy);

            Actor other = Itchy.isCtrlDown() ? this.target : this.opposite.getActor();

            double scaleX = (other.getX() - getActor().getX()) / (other.getX() - this.startX);
            double scaleY = (other.getY() - getActor().getY()) / (other.getY() - this.startY);
            double scale = Math.min(scaleX, scaleY);

            if (!Itchy.isCtrlDown()) {
                this.target.moveBy(dx * ratioX, dy * ratioY);
            }

            if (this.target.getAppearance().getPose() instanceof TextPose) {
                TextPose pose = (TextPose) this.target.getAppearance().getPose();
                double newFontSize = this.startScale * scale;
                if (newFontSize < 5) {
                    super.moveBy(-dx, -dy);
                    return;
                }
                pose.setFontSize(newFontSize);
            } else {
                double newScale = this.startScale * scale;
                double width = getActor().getAppearance().getPose().getSurface().getWidth() *
                    newScale;
                double height = getActor().getAppearance().getPose().getSurface().getHeight() *
                    newScale;
                if ((width < 4) || (height < 4)) {
                    super.moveBy(-dx, -dy);
                    return;
                }
                this.target.getAppearance().setScale(this.startScale * scale);
            }
        }

    }

    class RotateHandleBehaviour extends HandleBehaviour
    {
        @Override
        public void moveBy( int dx, int dy )
        {
            super.moveBy(dx, dy);

            double tx = getActor().getX() - this.target.getX();
            double ty = getActor().getY() - this.target.getY();

            double angleRadians = Math.atan2(ty, tx);
            double headingDiff = this.target.getHeading() - this.target.getAppearance().getDirection();
            getActor().setDirectionRadians(angleRadians);

            this.target.getAppearance().setDirectionRadians(angleRadians);
            this.target.setHeading(this.target.getAppearance().getDirection() + headingDiff);
        }

        @Override
        public void tick()
        {
            if (this.dragging) {
                return;
            }

            if (this.target != null) {
                getActor().moveTo(this.target);
                getActor().setDirection(this.target.getAppearance().getDirection());
                getActor().moveForwards(30);
            }
        }

    }

    class HeadingHandleBehaviour extends HandleBehaviour
    {
        @Override
        public void moveBy( int dx, int dy )
        {
            super.moveBy(dx, dy);

            double tx = getActor().getX() - this.target.getX();
            double ty = getActor().getY() - this.target.getY();

            double angle = Math.atan2(ty, tx);
            getActor().getAppearance().setDirectionRadians(angle);
            this.target.setHeadingRadians(angle);
        }

        @Override
        public void tick()
        {
            if (this.dragging) {
                return;
            }

            if (this.target != null) {
                getActor().moveTo(this.target);
                getActor().setDirection(this.target.getHeading());
                getActor().moveForwards(60);
            }
        }

    }
}
