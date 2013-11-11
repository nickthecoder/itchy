/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0 which accompanies this
 * distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

import java.util.ArrayList;
import java.util.List;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.NullComponent;
import uk.co.nickthecoder.itchy.gui.ReflectionTableModelRow;
import uk.co.nickthecoder.itchy.gui.SimpleTableModel;
import uk.co.nickthecoder.itchy.gui.SingleColumnRowComparator;
import uk.co.nickthecoder.itchy.gui.Table;
import uk.co.nickthecoder.itchy.gui.TableModel;
import uk.co.nickthecoder.itchy.gui.TableModelColumn;
import uk.co.nickthecoder.itchy.gui.TableModelRow;
import uk.co.nickthecoder.itchy.gui.TextBox;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.itchy.util.AbstractProperty;

public class AnimationsEditor extends SubEditor<AnimationResource>
{
    private Animation currentAnimation;

    private Container treeContainer;

    public AnimationsEditor( Editor editor )
    {
        super(editor);
    }

    @Override
    public Table createTable()
    {
        TableModelColumn name = new TableModelColumn("Name", 0, 200);
        name.rowComparator = new SingleColumnRowComparator<String>(0);

        List<TableModelColumn> columns = new ArrayList<TableModelColumn>();
        columns.add(name);

        TableModel tableModel = this.createTableModel();
        Table table = new Table(tableModel, columns);

        return table;
    }

    @Override
    public SimpleTableModel createTableModel()
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

    @Override
    protected void createForm()
    {
        super.createForm();
        this.currentAnimation = this.currentResource.animation.copy();

        this.treeContainer = new Container();
        this.createTree();
        this.form.grid.addRow(new NullComponent(), this.treeContainer);
    }

    private void createTree()
    {
        this.treeContainer.clear();
        this.treeContainer.addChild(this.createAnimationTree(this.currentAnimation, null));
    }

    private AnimationEditor createAnimationEditor( Animation animation )
    {
        AnimationEditor result;

        if (animation instanceof FramedAnimation) {
            FramedAnimation framedAnimation = (FramedAnimation) animation;
            result = new FramedAnimationEditor(this.editor, this.editor.resources, framedAnimation);

        }
        if (animation instanceof CompoundAnimation) {
            result = new AnimationEditor(this.editor, animation) {
                @Override
                public void onOk()
                {
                    super.onOk();
                    createTree();
                }
            };

        } else {
            result = new AnimationEditor(this.editor, animation);
        }

        return result;
    }

    private Component createAnimationTree( final Animation animation, final CompoundAnimation parent )
    {

        Container line = new Container();
        line.setFill(true, false);

        Button name = new Button(animation.getName());
        name.setExpansion(1);
        line.addChild(name);

        if ((parent != null) && (parent.children.get(0) != animation)) {
            Button up = new Button(new ImageComponent(this.editor.getStylesheet().resources.getPose("icon_up").getSurface()));
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
            Button delete = new Button(new ImageComponent(this.editor.getStylesheet().resources.getPose("icon_delete").getSurface()));

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

        name.addActionListener(new ActionListener() {
            @Override
            public void action()
            {
                AnimationsEditor.this.createAnimationEditor(animation).show();
            }
        });

        if (animation instanceof CompoundAnimation) {
            final CompoundAnimation ca = (CompoundAnimation) animation;

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
    protected void remove( AnimationResource ar )
    {
        this.editor.resources.removeAnimation(ar.getName());
    }

    @Override
    protected void onAdd()
    {
        Animation animation = new CompoundAnimation(true);

        this.edit(new AnimationResource(this.editor.resources, "", animation), true);
    }

    @Override
    protected void update() throws MessageException
    {
        TextBox name = (TextBox) this.form.getComponent("name");
        if (this.adding || (!name.getText().equals(this.currentResource.getName()))) {

            if (this.editor.resources.getAnimation(name.getText()) != null) {
                throw new MessageException("That name is already being used.");
            }
        }

        super.update();
        this.currentResource.animation = this.currentAnimation;

        if (this.adding) {
            this.editor.resources.addAnimation(this.currentResource);
        }
    }

    @Override
    protected List<AbstractProperty<AnimationResource, ?>> getProperties()
    {
        return AnimationResource.properties;
    }

}
