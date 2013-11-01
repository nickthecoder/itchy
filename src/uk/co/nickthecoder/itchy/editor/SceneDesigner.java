/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.nickthecoder.itchy.Actor;
import uk.co.nickthecoder.itchy.ActorsLayer;
import uk.co.nickthecoder.itchy.Appearance;
import uk.co.nickthecoder.itchy.Behaviour;
import uk.co.nickthecoder.itchy.CompoundLayer;
import uk.co.nickthecoder.itchy.Costume;
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.Layer;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.NullBehaviour;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneActor;
import uk.co.nickthecoder.itchy.SceneBehaviour;
import uk.co.nickthecoder.itchy.SceneResource;
import uk.co.nickthecoder.itchy.ScrollableLayer;
import uk.co.nickthecoder.itchy.TextPose;
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
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.GuiPose;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.MessageBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
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
import uk.co.nickthecoder.itchy.util.AbstractProperty;
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
    private static SceneActor copiedActor;

    private final Editor editor;

    private final SceneResource sceneResource;

    private Scene scene;

    private CompoundLayer designLayers;

    private ScrollableLayer currentDesignLayer;

    private ScrollableLayer glassLayer;

    private ActorsLayer guiLayer;

    private final Rect sceneRect;

    private final RGBA sceneBackground = new RGBA(0, 0, 0);

    private final RGBA guiBackground = null;

    private GuiPose toolboxPose;

    private GuiPose toolbarPose;

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
            this.onDone();
            return;
        }

        this.costumeButtonGroup = new ButtonGroup();

    }

    public void go()
    {
        Rect rect = new Rect(0, 0, this.editor.getWidth(), this.editor.getHeight());

        this.designLayers = new CompoundLayer("design", rect, this.sceneBackground);

        for (Layer gameLayer : Editor.singleton.game.getLayers().getChildren()) {
            if ((gameLayer instanceof
                ScrollableLayer) && (!gameLayer.locked)) {

                ScrollableLayer designLayer = new ScrollableLayer(gameLayer.getName(), rect);

                this.designLayers.add(designLayer);
                this.currentDesignLayer = designLayer;
            }
        }

        this.glassLayer = new ScrollableLayer("glass", rect);
        this.guiLayer = new ScrollableLayer("gui", rect, this.guiBackground);
        this.guiLayer.setYAxisPointsDown(true);

        this.editor.getLayers().add(this.designLayers);
        this.editor.getLayers().add(this.glassLayer);
        this.editor.getLayers().add(this.guiLayer);

        this.createToolbar();
        this.createToolbox();

        Actor toolboxActor = this.toolboxPose.getActor();
        this.guiLayer.addTop(toolboxActor);

        Actor toolbarActor = this.toolbarPose.getActor();
        this.guiLayer.addTop(toolbarActor);

        // Can be ANY of the scrolling layers, so I picked the first for convenience.
        this.designLayers.getChildren().get(0).addMouseListener(this, this.editor);
        Itchy.getGame().addKeyListener(this);

        this.scene.create(this.designLayers, this.editor.resources, true);

        this.createPageBorder();
        this.createHandles();

        this.setMode(MODE_SELECT);
        toolboxActor.moveTo(0, this.editor.getHeight() - this.toolboxPose.getHeight());

        this.onCenter();
    }

    private void onDone()
    {
        this.editor.getLayers().remove(this.designLayers);
        this.editor.getLayers().remove(this.glassLayer);
        this.editor.getLayers().remove(this.guiLayer);

        Itchy.getGame().removeKeyListener(this);
        this.designLayers.getChildren().get(0).removeMouseListener(this, this.editor);

        this.toolboxPose.destroy();
        this.toolbarPose.destroy();

        this.editor.mainGuiPose.show();
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
        this.glassLayer.addTop(actor);
        if (actor.getYAxisPointsDown()) {
            actor.moveTo(margin, margin);
        } else {
            actor.moveTo(margin, this.sceneRect.height - margin);
        }

    }

    private void createToolbox()
    {
        this.toolboxPose = new GuiPose() {
            // Mouse clicks on the toolbox mustn't get propagated to the design area underneigth.
            @Override
            public boolean mouseDown( MouseButtonEvent event )
            {
                super.mouseDown(event);
                return true;
            }
        };
        this.toolboxPose.addStyle("toolbox");
        this.toolboxPose.draggable = true;

        this.toolboxPose.setStylesheet(this.editor.getStylesheet());
        this.toolboxPose.reStyle();
        this.toolboxPose.forceLayout();
        this.toolboxPose.setPosition(0, 0, this.editor.getWidth(), 200);
        this.toolboxPose.addStyle("semi");

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
        this.createSceneDetails();
        this.createSceneBehaviourProperties();
        Container sceneDetails1 = new Container();
        sceneDetails1.setLayout(new VerticalLayout());
        sceneDetails1.addChild(this.sceneDetailsContainer);
        sceneDetails1.addChild(this.scenePropertiesContainer);
        VerticalScroll sceneDetailsScroll = new VerticalScroll(sceneDetails1);

        this.createLayersTable();

        this.toolboxNotebook = new Notebook();
        this.toolboxNotebook.addPage(new Label("Scene"), sceneDetailsScroll);
        this.toolboxNotebook.addPage(new Label("Costumes"), costumesScroll);
        this.toolboxNotebook.addPage(new Label("Actor"), propertiesScroll);
        this.toolboxNotebook.addPage(new Label("Appearance"), appearanceScroll);
        this.toolboxNotebook.addPage(new Label("Behaviour"), behaviourScroll);
        this.toolboxNotebook.addPage(new Label("Layers"), layersScroll);

        this.toolboxPose.setFill(true, true);
        this.toolboxNotebook.setFill(true, true);
        this.toolboxNotebook.setExpansion(1);
        costumes.setExpansion(1);
        costumes.setFill(true, true);
        costumesScroll.setExpansion(1);
        costumesScroll.setFill(true, true);

        this.toolboxPose.addChild(this.toolboxNotebook);
    }

    private void createToolbar()
    {
        this.toolbarPose = new GuiPose() {
            @Override
            public boolean mouseDown( MouseButtonEvent event )
            {
                // Mouse clicks on the toolbar mustn't get propagated to the design area
                // underneigth.
                super.mouseDown(event);
                return true;
            }
        };

        this.toolbarPose.addStyle("toolbar");

        this.toolbarPose.setStylesheet(this.editor.getStylesheet());
        this.toolbarPose.reStyle();
        this.toolbarPose.forceLayout();

        this.addToolbarButtons(this.toolbarPose);
        // this.toolbarPose.setFill( true, true );

        this.toolbarPose.setPosition(0, 0, this.editor.getWidth(),
            this.toolbarPose.getRequiredHeight());
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
                SceneDesigner.this.onDone();
            }
        });
        toolbar.addChild(exit);

        Button save = createButton("save", "Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onSave();
            }
        });
        toolbar.addChild(save);

        Button test = createButton("test", "Test");
        test.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onTest();
            }
        });
        toolbar.addChild(test);

        Button home = createButton("center", "Center");
        home.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onCenter();
            }
        });
        toolbar.addChild(home);

        Button cut = createButton("cut", "Cut");
        cut.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onCopy();
                SceneDesigner.this.onActorDelete();
            }
        });
        toolbar.addChild(cut);

        Button copy = createButton("copy", "Copy");
        copy.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onCopy();
            }
        });
        toolbar.addChild(copy);

        Button paste = createButton("paste", "Paste");
        paste.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onPaste();
            }
        });
        toolbar.addChild(paste);

        Button actorUp = createButton("up", "Up");
        actorUp.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorUp();
            }
        });
        toolbar.addChild(actorUp);

        Button actorDown = createButton("down", "Down");
        actorDown.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorDown();
            }
        });
        toolbar.addChild(actorDown);

        Button actorTop = createButton("top", "Top");
        actorTop.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorTop();
            }
        });
        toolbar.addChild(actorTop);

        Button actorBottom = createButton("bottom", "Bottom");
        actorBottom.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorBottom();
            }
        });
        toolbar.addChild(actorBottom);

        Button actorUpLayer = createButton("moveUpLayer", "Up a Layer");
        actorBottom.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorUpLayer();
            }
        });
        toolbar.addChild(actorUpLayer);

        Button actorDownLayer = createButton("moveDownLayer", "Down a Layer");
        actorBottom.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorDownLayer();
            }
        });
        toolbar.addChild(actorDownLayer);

        Button actorUnrotate = createButton("unrotate", "Unrotate");
        actorUnrotate.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorUnrotate();
            }
        });
        toolbar.addChild(actorUnrotate);

        Button actorUnscale = createButton("unscale", "Scale = 1");
        actorUnscale.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onActorUnscale();
            }
        });
        toolbar.addChild(actorUnscale);

        Button textButton = createButton("text", "Text");
        textButton.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                SceneDesigner.this.onText();
            }
        });
        toolbar.addChild(textButton);

    }

    private void updateTabs()
    {
        createActorProperties();
        createBehaviourProperties();
        createAppearanceProperties();
        updateLayersTable();
    }

    private void createActorProperties()
    {
        GridLayout grid = new GridLayout(this.propertiesContainer, 2);
        this.propertiesContainer.clear();
        this.propertiesContainer.setLayout(grid);

        this.actorTextInput = null;

        if (this.currentActor != null) {

            for (AbstractProperty<Actor, ?> property : this.currentActor.getProperties()) {
                try {
                    Component component = property.createComponent(this.currentActor, true);
                    grid.addRow(property.label, component);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private TextBox sceneResourceName;
    private ClassNameBox sceneBehaviourName;
    private CheckBox checkBoxShowMouse;

    private void createSceneDetails()
    {
        final GridLayout grid = new GridLayout(this.sceneDetailsContainer, 2);
        this.sceneDetailsContainer.clear();
        this.sceneDetailsContainer.setLayout(grid);

        this.sceneResourceName = new TextBox(this.sceneResource.getName());
        grid.addRow(new Label("Name"), this.sceneResourceName);

        this.sceneBehaviourName = new ClassNameBox(
            this.editor.resources.scriptManager,
            this.scene.sceneBehaviourName,
            SceneBehaviour.class);

        this.sceneBehaviourName.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                ClassNameBox box = SceneDesigner.this.sceneBehaviourName;
                String value = box.getClassName().name;
                boolean ok = SceneDesigner.this.editor.game.resources
                    .registerSceneBehaviourClassName(value);

                box.addStyle("error", !ok);
                if (ok) {
                    SceneDesigner.this.scene.sceneBehaviourName = box.getClassName();
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

        grid.addRow("Scene Behaviour", this.sceneBehaviourName);

        this.checkBoxShowMouse = new CheckBox(this.scene.showMouse);
        grid.addRow("Show Mouse", this.checkBoxShowMouse);

        this.checkBoxShowMouse.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                SceneDesigner.this.scene.showMouse =
                    SceneDesigner.this.checkBoxShowMouse.getValue();
            }
        });

    }

    private void createSceneBehaviourProperties()
    {
        GridLayout grid = new GridLayout(this.scenePropertiesContainer, 2);
        this.scenePropertiesContainer.clear();
        this.scenePropertiesContainer.setLayout(grid);

        for (AbstractProperty<SceneBehaviour, ?> property : this.scene.sceneBehaviour
            .getProperties()) {

            try {
                Component component = property.createComponent(this.scene.sceneBehaviour, true);
                grid.addRow(property.label, component);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void createAppearanceProperties()
    {
        GridLayout grid = new GridLayout(this.appearanceContainer, 2);
        this.appearanceContainer.clear();
        this.appearanceContainer.setLayout(grid);

        if (this.currentActor != null) {
            for (AbstractProperty<Appearance, ?> property : this.currentActor.getAppearance()
                .getProperties()) {

                try {
                    Component component = property.createComponent(
                        this.currentActor.getAppearance(), true);
                    grid.addRow(property.label, component);

                    if ("pose.text".equals(property.access)) {
                        this.actorTextInput = component;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void updateLayersTable()
    {
        String layerName = this.currentActor == null ? "" : this.currentActor.getLayer().getName();

        for (int i = 0; i < this.layersTableModel.getRowCount(); i++) {
            SimpleTableModelRow row = (SimpleTableModelRow) this.layersTableModel.getRow(i);
            String name = (String) (row.getData(1));
            if (layerName.equals(name)) {
                this.layersTable.selectRow(row);
                break;
            }
        }
    }

    private void createBehaviourProperties()
    {
        final GridLayout grid = new GridLayout(this.behaviourContainer, 2);
        this.behaviourContainer.setLayout(grid);

        SceneDesignerBehaviour sdb = (SceneDesignerBehaviour) this.currentActor.getBehaviour();

        this.behaviourClassName = new ClassNameBox(
            this.editor.resources.scriptManager,
            sdb.actualBehaviour.getClassName(),
            Behaviour.class
            );

        this.behaviourClassName.addChangeListener(new ComponentChangeListener() {

            @Override
            public void changed()
            {
                ClassName className = SceneDesigner.this.behaviourClassName.getClassName();
                SceneDesignerBehaviour sdb = (SceneDesignerBehaviour) SceneDesigner.this.currentActor
                    .getBehaviour();
                try {
                    sdb.setBehaviourClassName(SceneDesigner.this.editor.resources, className);
                    SceneDesigner.this.createBehaviourProperties(grid);
                    SceneDesigner.this.editor.game.resources
                        .registerBehaviourClassName(className.name);
                    SceneDesigner.this.behaviourClassName.removeStyle("error");
                } catch (Exception e) {
                    SceneDesigner.this.behaviourClassName.addStyle("error");
                }
                System.out.println("Styles : " + SceneDesigner.this.behaviourClassName.getStyles());
            }
        });

        grid.addRow("Behaviour", this.behaviourClassName);
        createBehaviourProperties(grid);
    }

    private void createBehaviourProperties( GridLayout grid )
    {
        grid.clear();
        this.behaviourClassName.remove();
        grid.addRow("Behaviour", this.behaviourClassName);

        SceneDesignerBehaviour behaviour = (SceneDesignerBehaviour) this.currentActor
            .getBehaviour();

        for (AbstractProperty<Behaviour, ?> property : behaviour.actualBehaviour.getProperties()) {
            try {
                Component component = property.createComponent(behaviour.actualBehaviour, true);
                grid.addRow(property.label, component);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createLayersTable()
    {
        this.layersTableModel = new SimpleTableModel();
        for (Layer layer : this.designLayers.getChildren()) {
            if (!layer.locked) {
                SimpleTableModelRow row = new SimpleTableModelRow();
                row.add(layer);
                row.add(layer.getName());
                this.layersTableModel.addRow(row);
            }
        }

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>(1);

        TableModelColumn showHideColumn = new TableModelColumn("Show", 0, 70) {
            public void addPlainCell( Container container, final TableModelRow row )
            {
                final Layer layer = (Layer) row.getData(0);
                final CheckBox hideShow = new CheckBox(layer.isVisible());
                hideShow.addChangeListener(new ComponentChangeListener() {
                    @Override
                    public void changed()
                    {
                        layer.setVisible(hideShow.getValue());
                    }
                });
                container.addChild(hideShow);
            }

            @Override
            public Component createCell( TableModelRow row )
            {
                Container container = new Container();
                this.addPlainCell(container, row);
                return container;
            };

            @Override
            public void updateComponent( Component component, TableModelRow row )
            {
                Container container = (Container) component;
                container.clear();
                this.addPlainCell(container, row);
            };
        };
        columns.add(showHideColumn);

        TableModelColumn nameColumn = new TableModelColumn("Layer", 1, 300);
        columns.add(nameColumn);

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
                SceneDesigner.this.currentDesignLayer = (ScrollableLayer) tableRow
                    .getTableModelRow().getData(0);
                // SceneDesigner.this.selectActor(null);
            }

            @Override
            public void onRowPicked( TableRow tableRow )
            {
            }
        });
    }

    private void deleteProperties()
    {
        this.propertiesContainer.clear();
        this.behaviourContainer.clear();
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
                    SceneDesigner.this.onSelectCostume(costumeResource);
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
        this.deleteStampActor();

        if (this.mode == MODE_SELECT) {
            this.costumeButtonGroup.select(this.selectButton);
        }
    }

    private void onSelectCostume( CostumeResource costumeResource )
    {
        this.deleteStampActor();
        this.selectActor(null);
        this.currentCostume = costumeResource.costume;
        this.setMode(MODE_STAMP_COSTUME);
        this.createStampActor();
    }

    @Override
    public boolean onKeyDown( KeyboardEvent event )
    {

        if (event.symbol == Keys.ESCAPE) {
            this.onEscape();
            return true;
        }

        if (Itchy.isCtrlDown()) {

            int scrollAmount = Itchy.isShiftDown() ? 100 : 10;

            if (event.symbol == Keys.s) {
                this.onSave();
                return true;

            } else if (event.symbol == Keys.w) {
                this.onDone();
                return true;

            } else if (event.symbol == Keys.x) {
                this.onCopy();
                this.onActorDelete();
                return true;

            } else if (event.symbol == Keys.c) {
                this.onCopy();
                return true;

            } else if (event.symbol == Keys.v) {
                this.onPaste();
                return true;
            } else if (event.symbol == Keys.LEFT) {
                this.scrollBy(-scrollAmount, 0);
                return true;

            } else if (event.symbol == Keys.RIGHT) {
                this.scrollBy(scrollAmount, 0);
                return true;

            } else if (event.symbol == Keys.UP) {
                this.scrollBy(0, scrollAmount);
                return true;

            } else if (event.symbol == Keys.DOWN) {
                this.scrollBy(0, -scrollAmount);
                return true;

            } else if (event.symbol == Keys.DELETE) {
                this.onActorDelete();
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
                this.onCenter();
                return true;

            } else if (event.symbol == Keys.PAGEUP) {
                this.onActorUpLayer();
                return true;

            } else if (event.symbol == Keys.PAGEDOWN) {
                this.onActorDownLayer();
                return true;

            } else if (event.symbol == Keys.o) {
                this.onActorUnrotate();
                return true;

            } else if (event.symbol == Keys.KEY_0) {
                this.onActorUnscale();
                return true;

            } else if (event.symbol == Keys.s) {
                this.onSave();
                return true;
            }

        } else {

            int moveAmount = Itchy.isShiftDown() ? 10 : 1;

            if (event.symbol == Keys.PAGEUP) {
                this.onActorUp();
                return true;

            } else if (event.symbol == Keys.PAGEDOWN) {
                this.onActorDown();
                return true;

            } else if (event.symbol == Keys.HOME) {
                this.onActorTop();
                return true;

            } else if (event.symbol == Keys.END) {
                this.onActorBottom();
                return true;

            } else if (event.symbol == Keys.LEFT) {
                this.moveActor(-moveAmount, 0);
                return true;

            } else if (event.symbol == Keys.RIGHT) {
                this.moveActor(moveAmount, 0);
                return true;

            } else if (event.symbol == Keys.UP) {
                this.moveActor(0, moveAmount);
                return true;

            } else if (event.symbol == Keys.DOWN) {
                this.moveActor(0, -moveAmount);
                return true;

            } else if (event.symbol == Keys.F8) {
                this.onEditText();
                return true;

            } else if (event.symbol == Keys.F12) {
                this.onTest();
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
        if ((event.button == 2) || ((event.button == 1) && Itchy.isShiftDown())) {
            this.setMode(MODE_DRAG_SCROLL);
            this.beginDrag(event);
            return true;
        }

        if (event.button != 1) {
            return false;
        }

        if (this.mode == MODE_SELECT) {

            for (HandleBehaviour handleBehaviour : this.handles) {
                Actor actor = handleBehaviour.getActor();

                if (actor.hitting(event.x, event.y)) {
                    this.beginDrag(event);
                    handleBehaviour.dragStart();
                    this.currentHandleBehaviour = handleBehaviour;
                    this.setMode(MODE_DRAG_HANDLE);
                    this.hideHighlightActor();
                    return true;
                }
            }

            boolean fromBottom = false; // Itchy.isShiftDown();

            if (Itchy.isCtrlDown()) {

                for (Layer child : fromBottom ?
                    this.designLayers.getChildren() :
                    Reversed.list(this.designLayers.getChildren())) {

                    ActorsLayer layer = (ActorsLayer) child;

                    for (Iterator<Actor> i = fromBottom ? layer.getActors().iterator() : layer
                        .getActors().descendingIterator(); i.hasNext();) {

                        Actor actor = i.next();

                        if (actor.hitting(event.x, event.y)) {
                            this.selectActor(actor);
                            this.setMode(MODE_DRAG_ACTOR);
                            this.beginDrag(event);
                            return true;
                        }
                    }
                }

            } else {

                for (Iterator<Actor> i = fromBottom ? this.currentDesignLayer.getActors()
                    .iterator() : this.currentDesignLayer
                    .getActors().descendingIterator(); i.hasNext();) {

                    Actor actor = i.next();

                    if (actor.hitting(event.x, event.y)) {
                        this.selectActor(actor);
                        this.setMode(MODE_DRAG_ACTOR);
                        this.beginDrag(event);
                        return true;
                    }
                }
            }

            this.selectActor(null);
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
            if (actor.getZOrder() == 0) {
                this.currentDesignLayer.addTop(actor);
            } else {
                this.currentDesignLayer.add(actor);
            }

            if (!Itchy.isShiftDown()) {
                this.setMode(MODE_SELECT);
                this.selectActor(actor);

                this.onEditText();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseUp( MouseButtonEvent event )
    {
        if ((this.mode == MODE_DRAG_HANDLE) || (this.mode == MODE_DRAG_ACTOR)) {
            this.updateTabs();
        }

        if (this.mode == MODE_DRAG_HANDLE) {
            this.currentHandleBehaviour.dragEnd();
            this.currentHandleBehaviour = null;
            this.setMode(MODE_SELECT);
            this.selectActor(this.currentActor);
        }

        if ((this.mode == MODE_DRAG_SCROLL) || (this.mode == MODE_DRAG_ACTOR)) {
            this.setMode(MODE_SELECT);
        }

        return false;
    }

    @Override
    public boolean onMouseMove( MouseMotionEvent event )
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
            this.beginDrag(event);

        } else if (this.mode == MODE_DRAG_HANDLE) {
            this.currentHandleBehaviour.moveBy(dx, dy);
            this.beginDrag(event);

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
        this.deleteHighlightActor();
        this.deleteProperties();

        if (actor != null) {

            this.updateTabs();
            this.createHightlightActor();
        }
    }

    private void createStampActor()
    {
        assert (this.stampActor == null);
        this.stampActor = new Actor(this.currentCostume);

        this.stampActor.moveTo(-10000, -10000); // Anywhere off screen
        this.stampActor.getAppearance().setAlpha(128);
        this.glassLayer.addTop(this.stampActor);
    }

    private void deleteStampActor()
    {
        if (this.stampActor != null) {
            this.glassLayer.remove(this.stampActor);
            this.stampActor = null;
        }
    }

    private void scrollBy( int dx, int dy )
    {
        for (Layer layer : this.designLayers.getChildren()) {
            ((ScrollableLayer) layer).scrollBy(dx, dy);
        }
        this.glassLayer.scrollBy(dx, dy);
    }

    private void onCenter()
    {
        int x = 0;
        int y = this.sceneRect.height - this.editor.getHeight() + this.toolbarPose.getHeight();
        for (Layer layer : this.designLayers.getChildren()) {
            ((ScrollableLayer) layer).scrollTo(x, y);
        }
        this.glassLayer.scrollTo(x, y);
    }

    private ActorsLayer getLayerBelow( ActorsLayer layer )
    {
        Layer result = null;
        for (Layer tmpLayer : this.designLayers.getChildren()) {
            if (layer == tmpLayer) {
                return (ActorsLayer) result;
            }
            result = tmpLayer;
        }

        return null;
    }

    private ActorsLayer getLayerAbove( ActorsLayer layer )
    {
        boolean found = false;
        for (Layer tmpLayer : this.designLayers.getChildren()) {
            if (found) {
                return (ActorsLayer) tmpLayer;
            }
            if (layer == tmpLayer) {
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
            this.currentDesignLayer.addTop(actor);
            this.selectActor(actor);
        }
    }

    private void onActorDelete()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.kill();
            this.selectActor(null);
        }
    }

    private void onActorUpLayer()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            ActorsLayer otherLayer = getLayerAbove(this.currentActor.getLayer());

            if (otherLayer != null) {
                this.currentActor.getLayer().remove(this.currentActor);
                otherLayer.addTop(this.currentActor);
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
            this.selectActor(this.currentActor);
        }
    }

    private void onActorUnscale()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            this.currentActor.getAppearance().setScale(1);
            this.selectActor(this.currentActor);
        }
    }

    private void onActorDownLayer()
    {
        if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
            ActorsLayer otherLayer = getLayerBelow(this.currentActor.getLayer());

            if (otherLayer != null) {
                this.currentActor.getLayer().remove(this.currentActor);
                otherLayer.addTop(this.currentActor);
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

        scene.sceneBehaviourName = this.scene.sceneBehaviourName;
        scene.sceneBehaviour = this.scene.sceneBehaviour;

        for (Layer child : this.designLayers.getChildren()) {
            ActorsLayer layer = (ActorsLayer) child;

            Scene.SceneLayer sceneLayer = scene.createSceneLayer(layer.getName());

            for (Actor actor : layer.getActors()) {
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

        this.selectActor(null);
        this.setMode(MODE_STAMP_COSTUME);

        TextPose pose = new TextPose(text, font, 22);
        this.stampActor = new Actor(pose);

        this.stampActor.moveTo(-10000, -10000); // Anywhere off screen
        this.glassLayer.addTop(this.stampActor);

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
        this.glassLayer.addTop(this.highlightActor);
        this.highlightActor.setBehaviour(new Follower(this.currentActor));
        this.highlightActor.activate();

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
        //this.rotateHandle.getActor().getAppearance().setAlpha(0);
        //this.headingHandle.getActor().getAppearance().setAlpha(0);
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
        rotateActor.activate();
        this.glassLayer.addTop(rotateActor);
        this.handles.add(this.rotateHandle);

        ImagePose headingPose = this.editor.getStylesheet().resources.getPose("headingHandle");
        Actor headingActor = new Actor(headingPose);
        this.headingHandle = new HeadingHandleBehaviour();
        headingActor.setBehaviour(this.headingHandle);
        headingActor.getAppearance().setAlpha(0);
        headingActor.activate();
        this.glassLayer.addTop(headingActor);
        this.handles.add(this.headingHandle);

        ImagePose scalePose = this.editor.getStylesheet().resources.getPose("scaleHandle");
        for (int dx = -1; dx < 2; dx += 2) {
            for (int dy = -1; dy < 2; dy += 2) {
                Actor scaleHandle = new Actor(scalePose);
                ScaleHandleBehaviour behaviour = new ScaleHandleBehaviour(dx, dy);
                scaleHandle.setBehaviour(behaviour);
                scaleHandle.getAppearance().setAlpha(0);
                scaleHandle.activate();

                this.scaleHandles.add(behaviour);
                this.handles.add(behaviour);
                this.glassLayer.addTop(scaleHandle);
            }
        }
        this.scaleHandles.get(0).opposite = this.scaleHandles.get(3);
        this.scaleHandles.get(3).opposite = this.scaleHandles.get(0);
        this.scaleHandles.get(1).opposite = this.scaleHandles.get(2);
        this.scaleHandles.get(2).opposite = this.scaleHandles.get(1);

    }

    abstract class HandleBehaviour extends Behaviour
    {
        Actor target;

        int startX;
        int startY;

        boolean dragging = false;

        public void setTarget( Actor target )
        {
            this.target = target;
            if (target == null) {
                this.getActor().getAppearance().setAlpha(0);
            } else {
                this.getActor().getAppearance().setAlpha(255);
            }
        }

        public void dragStart()
        {
            this.startX = (int) this.getActor().getX();
            this.startY = (int) this.getActor().getY();
            this.dragging = true;
        }

        public void dragEnd()
        {
            this.dragging = false;
        }

        public void moveBy( int dx, int dy )
        {
            this.getActor().moveBy(dx, dy);
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
                this.getActor().moveTo(x, y);
            }
        }

        @Override
        public void moveBy( int dx, int dy )
        {
            assert (this.target != null);

            double ratioX = (this.target.getX() - this.opposite.getActor().getX()) /
                (this.getActor().getX() - this.opposite.getActor().getX());
            double ratioY = (this.target.getY() - this.opposite.getActor().getY()) /
                (this.getActor().getY() - this.opposite.getActor().getY());

            super.moveBy(dx, dy);

            Actor other = Itchy.isCtrlDown() ? this.target : this.opposite.getActor();

            double scaleX = (other.getX() - this.getActor().getX()) / (other.getX() - this.startX);
            double scaleY = (other.getY() - this.getActor().getY()) / (other.getY() - this.startY);
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

            double tx = this.getActor().getX() - this.target.getX();
            double ty = this.getActor().getY() - this.target.getY();

            double angleRadians = Math.atan2(ty, tx);
            double headingDiff = this.target.getHeading() - this.target.getAppearance().getDirection();
            this.getActor().setDirectionRadians(angleRadians);

            this.target.getAppearance().setDirectionRadians(angleRadians);
            this.target.setHeading( this.target.getAppearance().getDirection() + headingDiff  );
        }

        @Override
        public void tick()
        {
            if (this.dragging) {
                return;
            }

            if (this.target != null) {
                this.getActor().moveTo(this.target);
                this.getActor().setDirection(this.target.getAppearance().getDirection());
                this.getActor().moveForward(30);
            }
        }

    }
    
    class HeadingHandleBehaviour extends HandleBehaviour
    {
        @Override
        public void moveBy( int dx, int dy )
        {
            super.moveBy(dx, dy);

            double tx = this.getActor().getX() - this.target.getX();
            double ty = this.getActor().getY() - this.target.getY();

            double angle = Math.atan2(ty, tx);
            this.getActor().getAppearance().setDirectionRadians(angle);
            this.target.setHeadingRadians(angle);
        }

        @Override
        public void tick()
        {
            if (this.dragging) {
                return;
            }

            if (this.target != null) {
                this.getActor().moveTo(this.target);
                this.getActor().setDirection(this.target.getHeading());
                this.getActor().moveForward(60);
            }
        }

    }

}
