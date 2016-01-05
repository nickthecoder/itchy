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
import uk.co.nickthecoder.itchy.CostumeResource;
import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Game;
import uk.co.nickthecoder.itchy.GenericCompoundView;
import uk.co.nickthecoder.itchy.ImagePose;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.KeyListener;
import uk.co.nickthecoder.itchy.MouseListener;
import uk.co.nickthecoder.itchy.Pose;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Role;
import uk.co.nickthecoder.itchy.Scene;
import uk.co.nickthecoder.itchy.SceneActor;
import uk.co.nickthecoder.itchy.SceneDirector;
import uk.co.nickthecoder.itchy.SceneResource;
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
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.FlowLayout;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.GuiView;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.Label;
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
import uk.co.nickthecoder.itchy.property.AbstractProperty;
import uk.co.nickthecoder.itchy.role.Follower;
import uk.co.nickthecoder.itchy.role.PlainRole;
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

public class SceneDesigner implements MouseListener, KeyListener {
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

	private GenericCompoundView<StageView> designViews;

	private StageView currentStageView;

	private ZOrderStage overlayStage;

	private StageView overlayView;

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

	private TextBox roleId;

	/**
	 * Has anything changed since onSave was last called?
	 */
	private boolean changed = false;

	final UndoList undoList;

	/**
	 * When a text actor is the current actor, then this will be the TextBox
	 * that you enter the Actor's text. This field is used to set the focus on
	 * it whenever a new text is added, and when a shortcut is used (F8).
	 */
	private Component actorTextInput;

	public SceneDesigner(Editor editor, SceneResource sceneResource) {
		this.undoList = new UndoList();
		this.editor = editor;
		this.sceneRect = new Rect(0, 0, editor.getGame().getWidth(), editor
				.getGame().getHeight());
		this.sceneResource = sceneResource;
		try {
			this.scene = this.sceneResource.loadScene();
		} catch (Exception e) {
			e.printStackTrace();
			exit();
			return;
		}

		this.costumeButtonGroup = new ButtonGroup();
	}

	public void go() {
		this.editor.root.hide();

		createToolbar();

		Rect wholeRect = new Rect(0, 0, this.editor.getWidth(),
				this.editor.getHeight());
		this.designViews = new GenericCompoundView<StageView>("designViews",
				wholeRect);

		Rect editRect = new Rect(0, this.toolbar.getHeight(),
				this.editor.getWidth(), this.editor.getHeight()
						- this.toolbar.getHeight());

		this.editor.getViews().add(this.designViews);

		for (Stage stage : this.editor.getGame().getStages()) {
			if (!stage.isLocked()) {
				Stage designStage = stage.createDesignStage();

				this.editor.getStages().add(designStage);
				StageView view = new StageView(editRect, designStage);
				this.designViews.add(view);
				this.currentStageView = view;
			}
		}

		this.overlayStage = new ZOrderStage("overlay");
		this.editor.getStages().add(this.overlayStage);

		this.overlayView = new StageView(editRect, this.overlayStage);
		this.editor.getViews().add(this.overlayView);

		this.editor.addMouseListener(this);
		this.editor.addKeyListener(this);

		this.scene.create(this.editor, true);

		createPageBorder();
		createHandles();

		createToolbox();

		setMode(MODE_SELECT);

		Surface screen = Itchy.getDisplaySurface();
		resize(screen.getWidth(), screen.getHeight());
		onCenter();
	}

	private void onDone() {
		if (this.changed) {
			QuestionBox question = new QuestionBox("Save", "Do you want save?") {
				@Override
				public boolean onOk() {
					onSave();
					exit();
					return true;
				}

				@Override
				public boolean onCancel() {
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

	private void exit() {
		this.sceneResource.name = this.oldSceneName;
		this.editor.clear();
		this.editor.sceneDesigner = null;
		this.editor.getStages().clear();
		this.overlayStage.clear();

		this.editor.getStages().remove(this.overlayStage);
		this.editor.getViews().remove(this.designViews);
		this.editor.getViews().remove(this.overlayView);
		this.editor.removeMouseListener(this);
		this.editor.removeKeyListener(this);

		Itchy.getGame().removeKeyListener(this);

		this.toolbox.hide();
		this.toolbar.hide();

		this.editor.root.show();
		this.editor.scenesEditor.refresh();
	}

	public void resize(int width, int height) {
		Rect editRect = new Rect(0, this.toolbar.getHeight(), width, height
				- this.toolbar.getHeight());

		this.overlayView.setPosition(editRect);
		this.designViews.setPosition(new Rect(0, 0, width, height));
		for (StageView view : this.designViews.getChildren()) {
			view.setPosition(editRect);
		}
		// System.out.println( "Design Views " + designViews.getPosition() );
		// System.out.println( "Design Views parent " +
		// designViews.getParent().getPosition() );
		// System.out.println( "Design Views parent parent " +
		// designViews.getParent().getParent().getPosition() );

		Rect old = this.toolbox.getView().getPosition();
		int y = old.y;
		if (y + old.height > height) {
			y = height - old.height;
		}
		Rect rect = new Rect(old.x, y, width, old.height);
		this.toolbox.getView().setPosition(rect);
		this.toolbar.getView().setPosition(
				new Rect(0, 0, width, this.toolbar.getHeight()));

		this.toolbar.setPosition(0, 0, width, this.toolbar.getHeight());
		this.toolbox.setPosition(0, 0, width, this.toolbox.getHeight());

	}

	private void createPageBorder() {
		int margin = 0;
		NinePatch ninePatch = this.editor.getStylesheet().resources
				.getNinePatch("pageBorder");
		Surface newSurface = ninePatch.createSurface(this.sceneRect.width
				+ margin * 2, this.sceneRect.height + margin * 2);

		ImagePose newPose = new ImagePose(newSurface);
		newPose.setOffsetX(margin);
		newPose.setOffsetY(margin);

		Actor actor = new Actor(newPose);
		Role role = new PlainRole();
		actor.setRole(role);
		this.overlayStage.addTop(actor);
		actor.moveTo(margin, this.sceneRect.height - margin);

	}

	private void createToolbox() {
		this.toolbox = new RootContainer();

		this.toolbox = new RootContainer();
		this.toolbox.addStyle("toolbox");
		this.toolbox.draggable = true;

		this.toolbox.setStylesheet(this.editor.getStylesheet());
		this.toolbox.reStyle();
		this.toolbox.forceLayout();
		this.toolbox.setPosition(0, 0, this.editor.getWidth(), 200);
		this.toolbox.addStyle("semi");

		PlainContainer costumes = new PlainContainer();
		costumes.addStyle("costumes");
		costumes.setLayout(new FlowLayout());

		for (String name : this.editor.resources.costumeNames()) {
			CostumeResource costumeResource = this.editor.resources
					.getCostumeResource(name);
			if (costumeResource.getCostume().showInDesigner) {
				this.addCostumeButton(costumes, costumeResource);
			}
		}
		VerticalScroll costumesScroll = new VerticalScroll(costumes);

		this.propertiesContainer = new PlainContainer();
		VerticalScroll propertiesScroll = new VerticalScroll(
				this.propertiesContainer);

		this.appearanceContainer = new PlainContainer();
		VerticalScroll appearanceScroll = new VerticalScroll(
				this.appearanceContainer);

		this.roleContainer = new PlainContainer();
		VerticalScroll roleScroll = new VerticalScroll(this.roleContainer);

		this.layersContainer = new PlainContainer();
		VerticalScroll layersScroll = new VerticalScroll(this.layersContainer);

		this.makeupContainer = new PlainContainer();
		VerticalScroll makeupScroll = new VerticalScroll(this.makeupContainer);

		this.sceneDetailsContainer = new PlainContainer();
		this.scenePropertiesContainer = new PlainContainer();
		this.createScenePage();
		this.createSceneDirectorProperties();
		PlainContainer sceneDetails1 = new PlainContainer();
		sceneDetails1.setLayout(new VerticalLayout());
		sceneDetails1.addChild(this.sceneDetailsContainer);
		sceneDetails1.addChild(this.scenePropertiesContainer);
		VerticalScroll sceneDetailsScroll = new VerticalScroll(sceneDetails1);

		createLayersTable();

		this.toolboxNotebook = new Notebook();
		this.toolboxNotebook.addPage(new Label("Scene"), sceneDetailsScroll)
				.setTooltip("ctrl+1");
		this.toolboxNotebook.addPage(new Label("Costumes"), costumesScroll)
				.setTooltip("ctrl+2");
		this.toolboxNotebook.addPage(new Label("Actor"), propertiesScroll)
				.setTooltip("ctrl+3");
		this.toolboxNotebook.addPage(new Label("Appearance"), appearanceScroll)
				.setTooltip("ctrl+4");
		this.toolboxNotebook.addPage(new Label("Makeup"), makeupScroll)
				.setTooltip("ctrl+5");
		this.toolboxNotebook.addPage(new Label("Role"), roleScroll).setTooltip(
				"ctrl+6");
		this.toolboxNotebook.addPage(new Label("Layers"), layersScroll)
				.setTooltip("ctrl+7");

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
		// this.toolbox.show();
		// this.toolbox.setPosition(0, this.editor.getHeight() - toolHeight,
		// this.editor.getWidth(), toolHeight);
		Rect rect = new Rect(0, this.editor.getHeight() - toolHeight,
				this.editor.getWidth(), toolHeight);
		GuiView view = new GuiView(rect, this.toolbox);
		Itchy.getGame().show(view);
	}

	private void createToolbar() {
		this.toolbar = new RootContainer();

		this.toolbar.addStyle("toolbar");

		this.toolbar.setStylesheet(this.editor.getStylesheet());
		this.toolbar.reStyle();
		this.toolbar.forceLayout();

		addToolbarButtons(this.toolbar);

		this.toolbar.setMinimumWidth(this.editor.getWidth());
		this.toolbar.show();
		this.toolbar.setPosition(0, 0, this.editor.getWidth(),
				this.toolbar.getRequiredHeight());
	}

	public Button createButton(String name, String text) {
		Pose pose = this.editor.getStylesheet().resources.getPose("icon_"
				+ name);
		if (pose == null) {
			return new Button(text);
		} else {
			ImageComponent image = new ImageComponent(pose.getSurface());
			return new Button(image);
		}
	}

	private void addToolbarButtons(Container toolbar) {

		Button exit = createButton("exit", "Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onDone();
			}
		});
		exit.setTooltip("Exit (ctrl+W)");
		toolbar.addChild(exit);

		Button save = createButton("save", "Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onSave();
			}
		});
		save.setTooltip("Save (ctrl+S)");
		toolbar.addChild(save);

		Button test = createButton("test", "Test");
		test.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onTest();
			}
		});
		test.setTooltip("Test (F12)");
		toolbar.addChild(test);

		Button home = createButton("center", "Center");
		home.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onCenter();
			}
		});
		home.setTooltip("Center View (ctrl+Home)");
		toolbar.addChild(home);

		Button cut = createButton("cut", "Cut");
		cut.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onCopy();
				onActorDelete();
			}
		});
		cut.setTooltip("Cut (ctrl+X)");
		toolbar.addChild(cut);

		Button copy = createButton("copy", "Copy");
		copy.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onCopy();
			}
		});
		copy.setTooltip("Copy (ctrl+C)");
		toolbar.addChild(copy);

		Button paste = createButton("paste", "Paste");
		paste.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onPaste();
			}
		});
		paste.setTooltip("Paste (ctrl+V)");
		toolbar.addChild(paste);

		Button actorUp = createButton("up", "Up");
		actorUp.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorUp();
			}
		});
		actorUp.setTooltip("Up the Z-Order (PageUp)");
		toolbar.addChild(actorUp);

		Button actorDown = createButton("down", "Down");
		actorDown.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorDown();
			}
		});
		actorDown.setTooltip("Down the Z-Order (PageDown)");
		toolbar.addChild(actorDown);

		Button actorTop = createButton("top", "Top");
		actorTop.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorTop();
			}
		});
		actorTop.setTooltip("Top of the Z-Order (Home)");
		toolbar.addChild(actorTop);

		Button actorBottom = createButton("bottom", "Bottom");
		actorBottom.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorBottom();
			}
		});
		actorBottom.setTooltip("Bottom of the Z-Order (End)");
		toolbar.addChild(actorBottom);

		Button actorUpLayer = createButton("moveUpLayer", "Up a Layer");
		actorUpLayer.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorUpStage();
			}
		});
		actorUpLayer.setTooltip("Move actor up a layer (ctrl+PageUp)");
		toolbar.addChild(actorUpLayer);

		Button actorDownLayer = createButton("moveDownLayer", "Down a Layer");
		actorDownLayer.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorDownStage();
			}
		});
		actorDownLayer.setTooltip("Move actor down a layer (ctrl+PageDown)");
		toolbar.addChild(actorDownLayer);

		Button actorUnrotate = createButton("unrotate", "Unrotate");
		actorUnrotate.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorUnrotate();
			}
		});
		actorUnrotate.setTooltip("Reset rotation (ctrl+o)");
		toolbar.addChild(actorUnrotate);

		Button actorUnscale = createButton("unscale", "Scale = 1");
		actorUnscale.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onActorUnscale();
			}
		});
		actorUnscale.setTooltip("Reset scale (ctrl+0)");
		toolbar.addChild(actorUnscale);

		Button textButton = createButton("text", "Text");
		textButton.addActionListener(new ActionListener() {
			@Override
			public void action() {
				onText();
			}
		});
		textButton.setTooltip("Add Text");
		toolbar.addChild(textButton);

	}

	private SceneDesignerPropertiesForm<SceneResource> sceneForm;

	private ClassNameBox sceneDirectorName;

	private String oldSceneName;

	private TextBox sceneNameBox;

	private void createScenePage() {
		this.sceneForm = new SceneDesignerPropertiesForm<SceneResource>(
				"scene", this, this.sceneResource, this.sceneResource.getProperties());
		this.sceneForm.autoUpdate = true;
		this.sceneDetailsContainer.clear();
		this.sceneDetailsContainer.addChild(this.sceneForm.createForm());

		this.sceneNameBox = (TextBox) this.sceneForm.getComponent("name");

		this.oldSceneName = this.sceneResource.name;

		this.sceneForm.addComponentChangeListener("name",
				new ComponentChangeListener() {
					@Override
					public void changed() {
						onSceneNameChanged(SceneDesigner.this.sceneNameBox);
					}
				});

		this.sceneDirectorName = (ClassNameBox) this.sceneForm
				.getComponent("sceneDirectorClassName");
		this.sceneForm.addComponentChangeListener("sceneDirectorClassName",
				new ComponentChangeListener() {
					@Override
					public void changed() {
						ClassNameBox box = SceneDesigner.this.sceneDirectorName;
						boolean ok = SceneDesigner.this.editor.resources
								.checkClassName(box.getClassName());
						if (ok) {
							SceneDesigner.this.scene.sceneDirectorClassName = box
									.getClassName();
							try {
								SceneDesigner.this.scene.sceneDirector = SceneDesigner.this.scene
										.createSceneDirector(SceneDesigner.this.editor.resources);

							} catch (Exception e) {
								e.printStackTrace();
								return;
							}
							createSceneDirectorProperties();
						}
					}
				});

	}

	private void onSceneNameChanged(TextBox textBox) {
		this.sceneNameBox.addStyle("error",
				!this.sceneResource.canRenameTo(textBox.getText()));
	}

	private SceneDesignerPropertiesForm<SceneDirector> sceneDirectorPropertiesForm;

	private void createSceneDirectorProperties() {
		this.sceneDirectorPropertiesForm = new SceneDesignerPropertiesForm<SceneDirector>(
				"sceneDirector", this, this.scene.sceneDirector,
				this.scene.sceneDirector.getProperties());

		this.sceneDirectorPropertiesForm.autoUpdate = true;
		this.sceneForm.grid.ungroup();
		this.sceneDirectorPropertiesForm.grid.groupWith(this.sceneForm.grid);
		this.scenePropertiesContainer.clear();
		this.scenePropertiesContainer.addChild(this.sceneDirectorPropertiesForm
				.createForm());
	}

	private SceneDesignerPropertiesForm<Actor> actorPropertiesForm;

	private void createActorPage() {
		this.propertiesContainer.clear();
		this.actorPropertiesForm = new SceneDesignerPropertiesForm<Actor>(
				"actor", this, this.currentActor,
				this.currentActor.getProperties());
		this.actorPropertiesForm.autoUpdate = true;
		this.propertiesContainer
				.addChild(this.actorPropertiesForm.createForm());
	}

	private SceneDesignerPropertiesForm<Appearance> appearancePropertiesForm;

	private void createAppearancePage() {
		Appearance appearance = this.currentActor.getAppearance();
		this.appearancePropertiesForm = new SceneDesignerPropertiesForm<Appearance>(
				"appearance", this, appearance, appearance.getProperties());
		this.appearancePropertiesForm.autoUpdate = true;
		this.appearanceContainer.clear();

		Costume costume = this.currentActor.getCostume();
		PlainContainer container = new PlainContainer();
		container.setType("form");
		GridLayout grid = new GridLayout(container, 2);
		grid.groupWith(this.appearancePropertiesForm.grid);
		final Label label = new Label((costume == null) ? "None"
				: this.editor.resources.getCostumeName(costume));
		final Button button = new Button(label);
		grid.addRow("Costume", button);
		button.addActionListener(new ActionListener() {
			@Override
			public void action() {
				CostumePicker picker = new CostumePicker(
						SceneDesigner.this.editor.resources, "none") {
					@Override
					public void pick(CostumeResource costumeResource) {
						if (costumeResource == null) {
							if (SceneDesigner.this.currentActor.getAppearance()
									.getPose() instanceof ImagePose) {
								return; // Image poses must use a costume (only
										// text poses don't need a costume)
							}
							label.setText("None");
							SceneDesigner.this.currentActor.setCostume(null);
						} else {
							label.setText(costumeResource.getName());
							SceneDesigner.this.currentActor
									.setCostume(costumeResource.getCostume());
							Pose pose = costumeResource.getCostume().getPose(
									SceneDesigner.this.currentActor
											.getStartEvent());
							if (pose != null) {
								SceneDesigner.this.currentActor.getAppearance()
										.setPose(pose);
							}
						}
					}
				};
				picker.show();
			}
		});

		Component theRest = this.appearancePropertiesForm.createForm();

		this.appearanceContainer.setLayout(new VerticalLayout());
		this.appearanceContainer.addChild(container);
		this.appearanceContainer.addChild(theRest);

		this.actorTextInput = this.appearancePropertiesForm
				.getComponent("pose.text");
	}

	private void createRolePage() {
		SceneDesignerRole sdb = (SceneDesignerRole) this.currentActor.getRole();

		this.roleClassName = new ClassNameBox(this.editor.getScriptManager(),
				sdb.actualRole.getClassName(), Role.class);

		this.roleClassName.addChangeListener(new ComponentChangeListener() {

			@Override
			public void changed() {
				ClassName className = SceneDesigner.this.roleClassName
						.getClassName();
				SceneDesignerRole sdb = (SceneDesignerRole) SceneDesigner.this.currentActor
						.getRole();
				try {
					sdb.setRoleClassName(SceneDesigner.this.editor.resources,
							className);
					SceneDesigner.this.createRoleProperties();
					SceneDesigner.this.editor.resources
							.checkClassName(className);
					SceneDesigner.this.roleClassName.removeStyle("error");

				} catch (Exception e) {
					SceneDesigner.this.roleClassName.addStyle("error");
				}
			}
		});

		Role role = ((SceneDesignerRole) SceneDesigner.this.currentActor
				.getRole()).actualRole;

		this.roleId = new TextBox(role.getId());

		this.roleId.addChangeListener(new ComponentChangeListener() {

			@Override
			public void changed() {
				Role role = ((SceneDesignerRole) SceneDesigner.this.currentActor
						.getRole()).actualRole;
				if (role != null) {
					role.setId(SceneDesigner.this.roleId.getText());
				}
			}
		});

		createRoleProperties();
	}

	private SceneDesignerPropertiesForm<Role> rolePropertiesForm;

	private void createRoleProperties() {
		this.roleClassName.remove();
		this.roleId.remove();

		Role role = ((SceneDesignerRole) this.currentActor.getRole()).actualRole;
		this.rolePropertiesForm = new SceneDesignerPropertiesForm<Role>("role",
				this, role, role.getProperties());
		this.rolePropertiesForm.autoUpdate = true;
		this.roleContainer.clear();
		this.rolePropertiesForm.grid.addRow("ID", this.roleId);
		this.rolePropertiesForm.grid.addRow("Role", this.roleClassName);

		this.roleContainer.addChild(this.rolePropertiesForm.createForm());
	}

	private ClassNameBox makeupClassName;

	private void createMakeupPage() {
		Makeup makeup = this.currentActor.getAppearance().getMakeup();

		this.makeupClassName = new ClassNameBox(this.editor.getScriptManager(),
				Appearance.getMakeupClassName(makeup), Makeup.class);
		this.makeupClassName.addChangeListener(new ComponentChangeListener() {

			@Override
			public void changed() {
				ClassName className = SceneDesigner.this.makeupClassName
						.getClassName();

				boolean ok = SceneDesigner.this.editor.resources
						.checkClassName(className);
				if (ok) {
					try {
						SceneDesigner.this.currentActor.getAppearance()
								.setMakeup(className);

						SceneDesigner.this.createMakeupProperties();
						SceneDesigner.this.makeupClassName.removeStyle("error");

					} catch (Exception e) {
						SceneDesigner.this.makeupClassName.addStyle("error");
					}
				}
			}
		});

		createMakeupProperties();
	}

	private SceneDesignerPropertiesForm<Makeup> makeupPropertiesForm;

	private void createMakeupProperties() {
		Makeup makeup = this.currentActor.getAppearance().getMakeup();
		this.makeupClassName.remove();
		this.makeupPropertiesForm = new SceneDesignerPropertiesForm<Makeup>(
				"makeup", this, makeup, makeup.getProperties());
		this.makeupPropertiesForm.autoUpdate = true;
		this.makeupContainer.clear();
		this.makeupPropertiesForm.grid.addRow("Makeup", this.makeupClassName);
		this.makeupContainer.addChild(this.makeupPropertiesForm.createForm());
	}

	private void updateProperties() {
		if (this.currentActor == null) {
			this.propertiesContainer.clear();
			this.appearanceContainer.clear();
			this.roleContainer.clear();
			this.actorTextInput = null;
			this.makeupContainer.clear();

			this.actorPropertiesForm = null;
			this.appearancePropertiesForm = null;
			this.rolePropertiesForm = null;
			this.makeupPropertiesForm = null;

		} else {
			createActorPage();
			createRolePage();
			createAppearancePage();
			createMakeupPage();
			updateLayersTable();
		}
	}

	private void createLayersTable() {
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
			public void addPlainCell(Container container,
					final TableModelRow row) {
				final StageView stageView = (StageView) row.getData(0);
				final CheckBox dim = new CheckBox(false);
				dim.addChangeListener(new ComponentChangeListener() {
					@Override
					public void changed() {
						stageView.maximumAlpha = dim.getValue() ? 80 : 255;
					}
				});
				container.addChild(dim);
			}

			@Override
			public AbstractComponent createCell(TableModelRow row) {
				PlainContainer container = new PlainContainer();
				addPlainCell(container, row);
				return container;
			};

			@Override
			public void updateComponent(Component component, TableModelRow row) {
				Container container = (Container) component;
				container.clear();
				addPlainCell(container, row);
			};
		};
		columns.add(showHideColumn);

		TableModelColumn nameColumn = new TableModelColumn("Layer", 1, 300);
		columns.add(nameColumn);

		TableModelColumn minAlphaColumn = new TableModelColumn("Reveal", 2, 100) {
			public void addPlainCell(Container container,
					final TableModelRow row) {
				final StageView stageView = (StageView) row.getData(0);
				final CheckBox check = new CheckBox(stageView.minimumAlpha > 0);
				check.addChangeListener(new ComponentChangeListener() {
					@Override
					public void changed() {
						try {
							stageView.minimumAlpha = (check.getValue() ? 200
									: 0);
						} catch (Exception e) {
						}
					}
				});
				container.addChild(check);
			}

			@Override
			public AbstractComponent createCell(TableModelRow row) {
				PlainContainer container = new PlainContainer();
				container.setXAlignment(0.5);
				addPlainCell(container, row);
				return container;
			};

			@Override
			public void updateComponent(Component component, TableModelRow row) {
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
			public void onRowSelected(TableRow tableRow) {
				SceneDesigner.this.currentStageView = (StageView) tableRow
						.getTableModelRow().getData(0);
			}

			@Override
			public void onRowPicked(TableRow tableRow) {
			}
		});
	}

	private void updateLayersTable() {
		String stageName = this.currentActor == null ? "" : this.currentActor
				.getStage().getName();

		for (int i = 0; i < this.layersTableModel.getRowCount(); i++) {
			SimpleTableModelRow row = (SimpleTableModelRow) this.layersTableModel
					.getRow(i);
			String name = (String) (row.getData(1));
			if (stageName.equals(name)) {
				this.layersTable.selectRow(row);
				break;
			}
		}
	}

	private void addCostumeButton(Container container,
			final CostumeResource costumeResource) {
		Surface surface = costumeResource.getThumbnail();
		if (surface != null) {
			ImageComponent img = new ImageComponent(surface);

			ToggleButton button = new ToggleButton(img);
			button.addActionListener(new ActionListener() {
				@Override
				public void action() {
					onSelectCostume(costumeResource);
				}
			});
			button.setTooltip(costumeResource.getName());
			this.costumeButtonGroup.add(button);
			container.addChild(button);
		}
	}

	@SuppressWarnings("rawtypes")
	public SceneDesignerPropertiesForm getForm(String formName) {
		if ("scene".equals(formName)) {
			return this.sceneForm;

		} else if ("sceneDirector".equals(formName)) {
			return this.sceneDirectorPropertiesForm;

		} else if ("actor".equals(formName)) {
			return this.actorPropertiesForm;

		} else if ("appearance".equals(formName)) {
			return this.appearancePropertiesForm;

		} else if ("role".equals(formName)) {
			return this.rolePropertiesForm;

		} else if ("makeup".equals(formName)) {
			return this.makeupPropertiesForm;

		}
		return null;
	}

	private void setMode(int mode) {
		if (mode == this.mode) {
			return;
		}

		this.mode = mode;
		deleteStampActor();

		if (this.mode == MODE_SELECT) {
			this.costumeButtonGroup.select(this.selectButton);
		}
	}

	private void onSelectCostume(CostumeResource costumeResource) {
		deleteStampActor();
		selectActor(null);
		this.currentCostume = costumeResource.getCostume();
		setMode(MODE_STAMP_COSTUME);
		createStampActor();
	}

	@Override
	public void onKeyDown(KeyboardEvent event) {

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
					this.undoList.redo();
				} else {
					this.undoList.undo();
				}
	            event.stopPropagation();

			} else if (event.symbol == Keys.y) {
				this.undoList.redo();
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
				this.toolboxNotebook.selectPage(event.symbol - Keys.KEY_1);
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
		if (this.currentActor != null) {
			double x = this.currentActor.getX() + dx;
			double y = this.currentActor.getY() + dy;

			StageConstraint sc = this.currentStageView.getStage()
					.getStageConstraint();
			this.currentActor.moveTo(sc.constrainX(x, y), sc.constrainY(x, y));
		}
	}

	@Override
	public void onKeyUp(KeyboardEvent event)
	{
	}

	@Override
	public void onMouseDown(MouseButtonEvent event) {
		try {
			if (!this.overlayView.adjustMouse(event)) {
				return;
			}
			mouseDown(event);
            event.stopPropagation();

		} finally {
			this.overlayView.unadjustMouse(event);
		}
	}

	@Override
	public void onMouseUp(MouseButtonEvent event) {
		try {
			if (!this.overlayView.adjustMouse(event)) {
				return;
			}
			mouseUp(event);
            event.stopPropagation();

		} finally {
			this.overlayView.unadjustMouse(event);
		}
	}

	@Override
	public void onMouseMove(MouseMotionEvent event) {
		try {
			if (!this.overlayView.adjustMouse(event)) {
				return;
			}
			mouseMove(event);
            event.stopPropagation();

		} finally {
			this.overlayView.unadjustMouse(event);
		}
	}

	private int previousClickX;
	private int previousClickY;

	private boolean isNearClick(MouseEvent event) {
		boolean result = (event.x - this.previousClickX)
				* (event.x - this.previousClickX)
				+ (event.y - this.previousClickY)
				* (event.y - this.previousClickY) < 10;

		this.previousClickX = event.x;
		this.previousClickY = event.y;

		return result;
	}

	public void mouseDown(MouseButtonEvent event) {
		boolean isNearClick = isNearClick(event);

		if ((event.button == 2) || ((event.button == 1) && Itchy.isAltDown())) {
			setMode(MODE_DRAG_SCROLL);
			beginDrag(event.x, event.y);
            event.stopPropagation();
		}

		if (event.button != 1) {
			return;
		}

		if (this.mode == MODE_SELECT) {

			for (HandleRole handleRole : this.handles) {
				Actor actor = handleRole.getActor();

				if (actor.hitting(event.x, event.y)
						&& (actor.getAppearance().getAlpha() > 0)) {
					beginDrag(event.x, event.y);
					handleRole.dragStart();
					this.currentHandleRole = handleRole;
					setMode(MODE_DRAG_HANDLE);
					this.hideHighlightActor();
		            event.stopPropagation();
				}
			}

			// Has the user repeatedly clicked to find the object BELOW the
			// currently selected one?
			// (or ABOVE, if shift is held down).
			boolean searching = isNearClick && Itchy.isShiftDown()
					&& (this.currentActor != null)
					&& (this.currentActor.hitting(event.x, event.y));

			if (Itchy.isCtrlDown()) {
				// Look at ALL stages, not only the current one.

				for (StageView child : Reversed.list(this.designViews
						.getChildren())) {
					Stage stage = child.getStage();

					for (Actor actor : Reversed.list(stage.getActors())) {

						if (actor.hitting(event.x, event.y)) {
							if (searching) {
								if (actor == this.currentActor) {
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

			} else {

				for (Actor actor : Reversed.list(this.currentStageView
						.getStage().getActors())) {

					if (actor.hitting(event.x, event.y)) {
						if (searching) {
							if (actor == this.currentActor) {
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

		if (this.mode == MODE_STAMP_COSTUME) {
			Actor actor;

			SceneDesignerRole role = new SceneDesignerRole();
			ClassName roleClassName;

			if (this.stampActor.getAppearance().getPose() instanceof TextPose) {
				actor = new Actor(this.stampActor.getAppearance().getPose());
				if (this.stampActor.getCostume() != null) {
					actor.setCostume(this.stampActor.getCostume());
					roleClassName = this.stampActor.getCostume().roleClassName;
				} else {
					roleClassName = new ClassName(Role.class,
							PlainRole.class.getName());
				}

			} else {
				actor = new Actor(this.currentCostume);
				roleClassName = this.stampActor.getCostume().roleClassName;
			}

			try {
				role.setRoleClassName(this.editor.resources, roleClassName);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!this.stampActor.isText()) {
				setDefaultProperties(role.actualRole, this.currentCostume);
			}

			StageConstraint sc = this.currentStageView.getStage()
					.getStageConstraint();

			actor.moveTo(sc.constrainX(event.x, event.y),
					sc.constrainY(event.x, event.y));
			actor.setRole(role);
			if (this.currentCostume != null) {
				actor.setZOrder(this.currentCostume.defaultZOrder);
			}
			// Place on top if no default zOrder defined for the costume.
			Stage stage = this.currentStageView.getStage();
			if (actor.getZOrder() == 0) {
				if (stage instanceof ZOrderStageInterface) {
					((ZOrderStageInterface) stage).addTop(actor);
				} else {
					stage.add(actor);
				}
			} else {
				stage.add(actor);
			}

			// TO DO, When stamping an actor implements undo/red, then the
			// StageConstraint will have to implement it too.
			sc.added(actor);

			if (!Itchy.isShiftDown()) {
				setMode(MODE_SELECT);
				selectActor(actor);

				onEditText();
			}
			this.changed = true;

            event.stopPropagation();
		}
	}

	public void mouseUp(MouseButtonEvent event) {
		if ((this.mode == MODE_DRAG_HANDLE) || (this.mode == MODE_DRAG_ACTOR)) {
			updateProperties();
		}

		if (this.mode == MODE_DRAG_HANDLE) {
			this.currentHandleRole.dragEnd();
			this.currentHandleRole = null;
			setMode(MODE_SELECT);
			selectActor(this.currentActor);
		}

		if ((this.mode == MODE_DRAG_SCROLL) || (this.mode == MODE_DRAG_ACTOR)) {
			setMode(MODE_SELECT);
		}
	}

	public void mouseMove(MouseMotionEvent event) {
		int dx = event.x - this.dragStartX;
		int dy = event.y - this.dragStartY;

		if (this.mode == MODE_STAMP_COSTUME) {

			StageConstraint sc = this.currentStageView.getStage()
					.getStageConstraint();

			double newX = sc.constrainX(event.x, event.y);
			double newY = sc.constrainY(event.x, event.y);

			this.stampActor.moveTo(newX, newY);

            event.stopPropagation();

		} else if (this.mode == MODE_DRAG_SCROLL) {
			scrollBy(-dx, -dy);
            event.stopPropagation();

		} else if (this.mode == MODE_DRAG_ACTOR) {

			StageConstraint sc = this.currentStageView.getStage()
					.getStageConstraint();

			double reqX = this.currentActor.getX() + dx;
			double reqY = this.currentActor.getY() + dy;

			double newX = sc.constrainX(reqX, reqY);
			double newY = sc.constrainY(reqX, reqY);

			if ((newX != this.currentActor.getX())
					|| (newY != this.currentActor.getY())) {
				this.undoList.apply(new UndoMoveActor(this.currentActor, newX
						- this.currentActor.getX(), newY
						- this.currentActor.getY()));
				this.changed = true;
				beginDrag((int) (event.x + newX - reqX),
						(int) (event.y + newY - reqY));
			}

		} else if (this.mode == MODE_DRAG_HANDLE) {
			this.currentHandleRole.moveBy(dx, dy);
			this.changed = true;
			beginDrag(event.x, event.y);

		}
	}

	private void beginDrag(int x, int y) {
		this.dragStartX = x;
		this.dragStartY = y;
	}

	private void setDefaultProperties(Role role, Costume costume) {
		for (AbstractProperty<Role, ?> property : role.getProperties()) {
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

	public void selectActor(Actor actor) {
		this.currentActor = actor;
		deleteHighlightActor();
		updateProperties();

		if (actor != null) {
			createHightlightActor();
		}
		this.toolbox.focus(); // Remove focus from any text boxes
	}

	public Actor getCurrentActor() {
		return this.currentActor;
	}

	private void createStampActor() {
		assert (this.stampActor == null);
		this.stampActor = new Actor(this.currentCostume);

		this.stampActor.moveTo(-10000, -10000); // Anywhere off screen
		this.stampActor.getAppearance().setAlpha(128);
		this.overlayStage.addTop(this.stampActor);
	}

	private void deleteStampActor() {
		if (this.stampActor != null) {
			this.overlayStage.remove(this.stampActor);
			this.stampActor = null;
		}
	}

	private void scrollBy(int dx, int dy) {
		for (StageView view : this.designViews.getChildren()) {
			view.scrollBy(dx, dy);
		}
		this.overlayView.scrollBy(dx, dy);
	}

	private void onCenter() {
		int x = 0;
		int y = this.sceneRect.height
				- (int) this.overlayView.getVisibleRectangle().height;
		for (StageView stageView : this.designViews.getChildren()) {
			stageView.scrollTo(x, y);
		}
		this.overlayView.scrollTo(x, y);
	}

	private Stage getStageBelow(Stage stage) {
		Stage result = null;
		for (View view : this.designViews.getChildren()) {
			if (view instanceof StageView) {
				if (((StageView) view).getStage() == stage) {
					return result;
				}
				result = ((StageView) view).getStage();
			}
		}

		return null;
	}

	private Stage getStageAbove(Stage stage) {
		boolean found = false;
		for (View view : this.designViews.getChildren()) {
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

	private void onActorUp() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			Stage stage = this.currentActor.getStage();
			if (stage instanceof ZOrderStageInterface) {
				((ZOrderStageInterface) (stage)).zOrderUp(this.currentActor);
			}
		}
	}

	private void onActorDown() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			Stage stage = this.currentActor.getStage();
			if (stage instanceof ZOrderStageInterface) {
				((ZOrderStageInterface) (stage)).zOrderDown(this.currentActor);
			}
			updateProperties();
		}
	}

	private void onActorTop() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			Stage stage = this.currentActor.getStage();
			if (stage instanceof ZOrderStageInterface) {
				((ZOrderStageInterface) (stage)).addTop(this.currentActor);
			}
			updateProperties();
		}
	}

	private void onActorBottom() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			Stage stage = this.currentActor.getStage();
			if (stage instanceof ZOrderStageInterface) {
				((ZOrderStageInterface) (stage)).addBottom(this.currentActor);
			}
			updateProperties();
		}
	}

	private void onCopy() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			copiedActor = SceneActor.createSceneActor(this.currentActor);
		}
	}

	private void onPaste() {
		if (SceneDesigner.copiedActor != null) {
			Actor actor = SceneDesigner.copiedActor.createActor(
					this.editor.resources, true);
			actor.moveBy(10, 10);
			addTop(this.currentStageView.getStage(), actor);
			selectActor(actor);
		}
	}

	private void onActorDelete() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			this.currentActor.kill();
			selectActor(null);
		}
	}

	private void onActorUpStage() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			Stage otherStage = getStageAbove(this.currentActor.getStage());

			if (otherStage != null) {
				this.currentActor.getStage().remove(this.currentActor);
				otherStage.add(this.currentActor);
				updateLayersTable();
			}
		}
	}

	private void onActorDownStage() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			Stage otherStage = getStageBelow(this.currentActor.getStage());

			if (otherStage != null) {
				this.currentActor.getStage().remove(this.currentActor);
				otherStage.add(this.currentActor);
				updateLayersTable();
			}
		}
	}

	private void onActorUnrotate() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			double direction = this.currentActor.getAppearance().getPose()
					.getDirection();
			if (this.currentActor.getHeading() == this.currentActor
					.getAppearance().getDirection()) {
				this.currentActor.setHeading(direction);
			}
			this.currentActor.setDirection(direction);
			selectActor(this.currentActor);
		}
	}

	private void onResetZOrders() {
		for (StageView child : this.designViews.getChildren()) {
			Stage stage = child.getStage();

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

	private void onActorUnscale() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			this.currentActor.getAppearance().setScale(1);
			selectActor(this.currentActor);
		}
	}

	private void onEditText() {
		if ((this.mode == MODE_SELECT) && (this.currentActor != null)) {
			if (this.actorTextInput != null) {
				this.actorTextInput.focus();
			}
		}
	}

	private void onSave() {
		this.scene.clear();
		for (StageView stageView : this.designViews.getChildren()) {

			Scene.SceneLayer sceneLayer = this.scene.createSceneLayer(stageView
					.getStage().getName());

			for (Actor actor : stageView.getStage().getActors()) {
				SceneActor sceneActor = SceneActor.createSceneActor(actor);
				sceneLayer.add(sceneActor);
			}
		}

		try {
			// Rename the scene first if needed.
			if (!this.sceneResource.name.equals(this.oldSceneName)) {
				String newName = this.sceneResource.name;
				this.sceneResource.name = this.oldSceneName;
				this.sceneResource.rename(newName);
				this.oldSceneName = newName;
			}

			this.sceneResource.setScene(this.scene);

			this.sceneResource.save();
			this.editor.resources.save();
			this.changed = false;

		} catch (Exception e) {
			e.printStackTrace();
			new MessageBox("Error", "Save failed.").show();
		}
	}

	private void onTest() {
		onSave();

		try {

			Resources duplicate = this.editor.resources.copy();
			Game game = duplicate.game;
			game.testScene(this.sceneResource.name);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onText() {
		Font font = this.editor.resources.getDefaultFont();
		if (font == null) {
			new MessageBox("Cannot add text without a font",
					"Exit the scene designer, and add a font to your game.")
					.show();
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

	private void addTop(Stage stage, Actor actor) {
		if (stage instanceof ZOrderStageInterface) {
			((ZOrderStageInterface) stage).addTop(actor);
		} else {
			stage.add(actor);
		}
	}

	private void createHightlightActor() {
		Surface actorSurface = this.currentActor.getAppearance().getSurface();

		int margin = 10;
		NinePatch ninePatch = this.editor.getStylesheet().resources
				.getNinePatch("highlight");
		Surface newSurface = ninePatch.createSurface(actorSurface.getWidth()
				+ margin * 2, actorSurface.getHeight() + margin * 2);

		ImagePose newPose = new ImagePose(newSurface);
		newPose.setOffsetX(this.currentActor.getAppearance().getOffsetX()
				+ margin);
		newPose.setOffsetY(this.currentActor.getAppearance().getOffsetY()
				+ margin);

		this.highlightActor = new Actor(newPose);
		this.highlightActor.moveTo(this.currentActor);
		this.overlayStage.addTop(this.highlightActor);
		this.highlightActor.setRole(new Follower(this.currentActor));

		for (ScaleHandleRole be : this.scaleHandles) {
			be.setTarget(this.currentActor);
			be.getActor().getAppearance().setAlpha(255);
		}
		this.rotateHandle.setTarget(this.currentActor);
		this.rotateHandle.getActor().getAppearance().setAlpha(255);

		this.headingHandle.setTarget(this.currentActor);
		this.headingHandle.getActor().getAppearance().setAlpha(255);

	}

	private void hideHighlightActor() {
		for (ScaleHandleRole be : this.scaleHandles) {
			be.getActor().getAppearance().setAlpha(0);
		}
		// this.rotateHandle.getActor().getAppearance().setAlpha(0);
		// this.headingHandle.getActor().getAppearance().setAlpha(0);
		if (this.highlightActor != null) {
			this.highlightActor.getAppearance().setAlpha(0);
		}
	}

	private void deleteHighlightActor() {
		if (this.highlightActor != null) {
			this.highlightActor.kill();
			this.highlightActor = null;
		}
		for (ScaleHandleRole be : this.scaleHandles) {
			be.setTarget(null);
		}
		this.rotateHandle.setTarget(null);
		this.headingHandle.setTarget(null);
	}

	private void createHandles() {

		ImagePose rotatePose = this.editor.getStylesheet().resources
				.getPose("rotateHandle");
		Actor rotateActor = new Actor(rotatePose);
		this.rotateHandle = new RotateHandleRole();
		rotateActor.setRole(this.rotateHandle);
		rotateActor.getAppearance().setAlpha(0);
		this.overlayStage.addTop(rotateActor);
		this.handles.add(this.rotateHandle);

		ImagePose headingPose = this.editor.getStylesheet().resources
				.getPose("headingHandle");
		Actor headingActor = new Actor(headingPose);
		this.headingHandle = new HeadingHandleRole();
		headingActor.setRole(this.headingHandle);
		headingActor.getAppearance().setAlpha(0);
		this.overlayStage.addTop(headingActor);
		this.handles.add(this.headingHandle);

		ImagePose scalePose = this.editor.getStylesheet().resources
				.getPose("scaleHandle");
		for (int dx = -1; dx < 2; dx += 2) {
			for (int dy = -1; dy < 2; dy += 2) {
				Actor scaleHandle = new Actor(scalePose);
				ScaleHandleRole role = new ScaleHandleRole(dx, dy);
				scaleHandle.setRole(role);
				scaleHandle.getAppearance().setAlpha(0);

				this.scaleHandles.add(role);
				this.handles.add(role);
				this.overlayStage.addTop(scaleHandle);
			}
		}
		this.scaleHandles.get(0).opposite = this.scaleHandles.get(3);
		this.scaleHandles.get(3).opposite = this.scaleHandles.get(0);
		this.scaleHandles.get(1).opposite = this.scaleHandles.get(2);
		this.scaleHandles.get(2).opposite = this.scaleHandles.get(1);

	}

	abstract class HandleRole extends AbstractRole {
		Actor target;

		int startX;
		int startY;

		boolean dragging = false;

		public void setTarget(Actor target) {
			this.target = target;
			if (target == null) {
				getActor().getAppearance().setAlpha(0);
			} else {
				getActor().getAppearance().setAlpha(255);
			}
		}

		public void dragStart() {
			this.startX = (int) getActor().getX();
			this.startY = (int) getActor().getY();
			this.dragging = true;
		}

		public void dragEnd() {
			this.dragging = false;
		}

		public void moveBy(int dx, int dy) {
			getActor().moveBy(dx, dy);
		}
	}

	class ScaleHandleRole extends HandleRole {
		int cornerX;
		int cornerY;
		double startScale;
		double startTargetX;
		double startTargetY;
		ScaleHandleRole opposite;

		public ScaleHandleRole(int dx, int dy) {
			this.cornerX = dx;
			this.cornerY = dy;
		}

		@Override
		public void dragStart() {
			super.dragStart();
			if (this.target.getAppearance().getPose() instanceof TextPose) {
				this.startScale = ((TextPose) (this.target.getAppearance()
						.getPose())).getFontSize();
			} else {
				this.startScale = this.target.getAppearance().getScale();
			}
			this.startTargetX = this.target.getX();
			this.startTargetY = this.target.getY();
		}

		@Override
		public void tick() {
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
		public void moveBy(int dx, int dy) {
			assert (this.target != null);
			UndoScaleActor undo = new UndoScaleActor(this.target);
			try {

				double ratioX = (this.target.getX() - this.opposite.getActor()
						.getX())
						/ (getActor().getX() - this.opposite.getActor().getX());
				double ratioY = (this.target.getY() - this.opposite.getActor()
						.getY())
						/ (getActor().getY() - this.opposite.getActor().getY());

				super.moveBy(dx, dy);

				Actor other = Itchy.isCtrlDown() ? this.target : this.opposite
						.getActor();

				double scaleX = (other.getX() - getActor().getX())
						/ (other.getX() - this.startX);
				double scaleY = (other.getY() - getActor().getY())
						/ (other.getY() - this.startY);
				double scale = Math.min(scaleX, scaleY);

				if (!Itchy.isCtrlDown()) {
					this.target.moveBy(dx * ratioX, dy * ratioY);
				}

				if (this.target.getAppearance().getPose() instanceof TextPose) {
					TextPose pose = (TextPose) this.target.getAppearance()
							.getPose();
					double newFontSize = this.startScale * scale;
					if (newFontSize < 5) {
						super.moveBy(-dx, -dy);
						return;
					}
					pose.setFontSize(newFontSize);
				} else {
					double newScale = this.startScale * scale;
					double width = getActor().getAppearance().getPose()
							.getSurface().getWidth()
							* newScale;
					double height = getActor().getAppearance().getPose()
							.getSurface().getHeight()
							* newScale;
					if ((width < 4) || (height < 4)) {
						super.moveBy(-dx, -dy);
						return;
					}
					this.target.getAppearance().setScale(
							this.startScale * scale);
				}
			} finally {
				undo.end(SceneDesigner.this.undoList);
			}
		}

	}

	class RotateHandleRole extends HandleRole {
		@Override
		public void moveBy(int dx, int dy) {
			if (this.target == null) {
				return;
			}

			UndoRotateActor undo = new UndoRotateActor(this.target);
			try {
				super.moveBy(dx, dy);

				double tx = getActor().getX() - this.target.getX();
				double ty = getActor().getY() - this.target.getY();

				double angleRadians = Math.atan2(ty, tx);
				double headingDiff = this.target.getHeading()
						- this.target.getAppearance().getDirection();
				getActor().setDirectionRadians(angleRadians);

				this.target.getAppearance().setDirectionRadians(angleRadians);
				this.target.setHeading(this.target.getAppearance()
						.getDirection() + headingDiff);

			} finally {
				undo.end(SceneDesigner.this.undoList);
			}
		}

		@Override
		public void tick() {
			if (this.dragging) {
				return;
			}

			if (this.target != null) {
				getActor().moveTo(this.target);
				getActor().setDirection(
						this.target.getAppearance().getDirection());
				getActor().moveForwards(30);
			}
		}

	}

	class HeadingHandleRole extends HandleRole {
		@Override
		public void moveBy(int dx, int dy) {
			UndoRotateActor undo = new UndoRotateActor(this.target);
			try {

				super.moveBy(dx, dy);

				double tx = getActor().getX() - this.target.getX();
				double ty = getActor().getY() - this.target.getY();

				double angle = Math.atan2(ty, tx);
				getActor().getAppearance().setDirectionRadians(angle);
				this.target.setHeadingRadians(angle);

			} finally {
				undo.end(SceneDesigner.this.undoList);
			}
		}

		@Override
		public void tick() {
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
