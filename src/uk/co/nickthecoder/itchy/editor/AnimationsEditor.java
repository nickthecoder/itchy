/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.animation.AlphaAnimation;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.ForwardsAnimation;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.animation.MoveAnimation;
import uk.co.nickthecoder.itchy.animation.ScaleAnimation;
import uk.co.nickthecoder.itchy.animation.TurnAnimation;
import uk.co.nickthecoder.itchy.gui.AbstractTableListener;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.GridLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.itchy.gui.NullComponent;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TableRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;

public class AnimationsEditor extends SubEditor
{
    private TextBox txtName;

    private AnimationResource currentAnimationResource;

    private Animation currentAnimation;

    private SimpleTableModel tableModel;

    private Container treeContainer;

    public AnimationsEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Container createPage()
    {
        Container form = super.createPage();
        form.setFill(true, true);

        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);

        this.tableModel = this.createTableModel();
        this.table = new Table(this.tableModel, columns);
        this.table.addTableListener(new AbstractTableListener() {
            @Override
            public void onRowPicked( TableRow tableRow )
            {
                AnimationsEditor.this.onEdit();
            }

        });

        this.table.sort(0);
        this.table.setFill(true, true);
        this.table.setExpansion(1.0);
        form.addChild(this.table);

        form.addChild(this.createListButtons());

        return form;
    }

    private SimpleTableModel createTableModel()
    {
        SimpleTableModel model = new SimpleTableModel();

        for (String animationName : this.editor.resources.animationNames()) {
            AnimationResource animationResource = this.editor.resources
                .getAnimationResource(animationName);
            String[] attributeNames = { "name" };
            TableModelRow row = new ReflectionTableModelRow<AnimationResource>(animationResource,
                attributeNames);
            model.addRow(row);
        }
        return model;
    }

    private void rebuildTable()
    {
        this.table.setTableModel(this.createTableModel());
    }

    @Override
    protected void edit( GridLayout grid, Object resource )
    {
        this.currentAnimationResource = (AnimationResource) resource;
        this.currentAnimation = this.currentAnimationResource.animation.copy();

        this.txtName = new TextBox(this.currentAnimationResource.getName());
        grid.addRow(new Label("Name"), this.txtName);

        this.treeContainer = new Container();
        this.createTree();
        grid.addRow(new NullComponent(), this.treeContainer);
    }

    private void createTree()
    {
        this.treeContainer.clear();
        this.treeContainer.addChild(this.createAnimationTree(this.currentAnimation, null));
    }

    private AnimationEditor createAnimationEditor( Animation animation )
    {
        if (animation instanceof MoveAnimation) {
            return new MoveAnimationEditor(this.editor, (MoveAnimation) animation);

        } else if (animation instanceof ForwardsAnimation) {
            return new ForwardAnimationEditor(this.editor, (ForwardsAnimation) animation);

        } else if (animation instanceof TurnAnimation) {
            return new TurnAnimationEditor(this.editor, (TurnAnimation) animation);

        } else if (animation instanceof ScaleAnimation) {
            return new ScaleAnimationEditor(this.editor, (ScaleAnimation) animation);

        } else if (animation instanceof AlphaAnimation) {
            return new AlphaAnimationEditor(this.editor, (AlphaAnimation) animation);

        } else if (animation instanceof FramedAnimation) {
            return new FramedAnimationEditor(this.editor, this.editor.resources,
                (FramedAnimation) animation);

        } else {
            return new AnimationEditor(this.editor, animation);
        }
    }

    private Component createAnimationTree( final Animation animation, final CompoundAnimation parent )
    {

        Container line = new Container();
        line.setFill(true, false);

        Button name = new Button(animation.getName());
        name.setExpansion(1);
        line.addChild(name);

        if ((parent != null) && (parent.children.get(0) != animation)) {
            Button up = new Button(new ImageComponent(this.editor.rules.resources.getPose(
                "icon_up").getSurface()));
            up.addStyle("compact");
            up.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    parent.moveAnimationUp(animation);
                    AnimationsEditor.this.createTree();
                }
            });
            line.addChild(up);
        }

        if (parent != null) {
            Button delete = new Button(new ImageComponent(this.editor.rules.resources.getPose(
                "icon_delete").getSurface()));
            delete.addStyle("compact");
            delete.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    parent.removeAnimation(animation);
                    AnimationsEditor.this.createTree();
                }
            });
            line.addChild(delete);
        }

        if (animation instanceof CompoundAnimation) {
            final CompoundAnimation ca = (CompoundAnimation) animation;

            name.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    ca.sequence = !ca.sequence;
                    AnimationsEditor.this.createTree();
                }
            });

            Container result = new Container();
            result.addChild(line);
            result.setLayout(new VerticalLayout());
            result.addStyle("panel");
            result.setFill(true, false);

            Container indent = new Container();
            indent.setFill(true, false);
            indent.setLayout(new VerticalLayout());
            indent.addStyle("animationIndent");
            result.addChild(indent);

            Container secondLine = new Container();
            secondLine.addStyle("form");
            secondLine.addChild(new Label("Loops"));
            final IntegerBox loops = new IntegerBox(ca.loops);
            loops.addChangeListener(new ComponentChangeListener() {
                @Override
                public void changed()
                {
                    try {
                        ca.loops = loops.getValue();
                    } catch (Exception e) {
                    }
                }
            });
            secondLine.addChild(loops);
            indent.addChild(secondLine);

            for (Animation child : ca.children) {
                indent.addChild(this.createAnimationTree(child, ca));
            }

            Button add = new Button("Add");
            add.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    AnimationsEditor.this.addAnimation(ca);
                }
            });
            indent.addChild(add);

            if (parent == null) {
                VerticalScroll vs = new VerticalScroll(result);
                // vs.setClientHeight( 100 );
                result.addStyle("animationTree");
                return vs;
            }

            return result;

        } else {

            name.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    AnimationsEditor.this.createAnimationEditor(animation).show();
                }
            });

            return line;
        }

    }

    private void addAnimation( final CompoundAnimation ca )
    {
        AnimationTypePicker picker = new AnimationTypePicker() {
            @Override
            public void pick( Animation animation )
            {
                Animation child = animation.copy();
                ca.addAnimation(child);
                AnimationsEditor.this.createTree();
                AnimationsEditor.this.createAnimationEditor(child).show();
            }
        };
        picker.show();
    }

    @Override
    protected void onOk()
    {
        if (this.adding ||
            (!this.txtName.getText().equals(this.currentAnimationResource.getName()))) {
            if (this.editor.resources.getAnimation(this.txtName.getText()) != null) {
                this.setMessage("That name is already being used.");
                return;
            }
        }
        this.currentAnimationResource.rename(this.txtName.getText());
        this.currentAnimationResource.animation = this.currentAnimation;

        if (this.adding) {
            this.rebuildTable();
        } else {

            this.table.updateRow(this.table.getCurrentTableModelRow());
        }

        Itchy.singleton.getGame().hideWindow(this.editWindow);
    }

    @Override
    protected void remove( Object resource )
    {
        AnimationResource ar = (AnimationResource) resource;

        this.editor.resources.removeAnimation(ar.getName());
        this.rebuildTable();
    }

    @Override
    protected void onAdd()
    {
        this.adding = true;

        this.currentAnimation = new CompoundAnimation(true);
        this.currentAnimationResource = new AnimationResource(this.editor.resources,
            "newAnimation", this.currentAnimation);

        this.showDetails(this.currentAnimationResource);
    }

}
