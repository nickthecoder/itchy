package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.gui.AbstractComponent;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.AnimationTypePicker;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.GuiButton;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.NullComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;

public class EditAnimation extends EditNamedSubject<AnimationResource>
{

    private PlainContainer treeContainer;

    public EditAnimation(Resources resources, ListSubjects<AnimationResource> listSubjects, AnimationResource subject,
        boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Animation";
    }

    @Override
    protected AnimationResource getSubjectByName(String name)
    {
        return resources.getAnimationResource(name);
    }

    @Override
    protected void add()
    {
        resources.addAnimation(subject);
    }

    @Override
    protected void rename()
    {
        resources.renameAnimation(subject);
    }

    @Override
    protected Component createForm()
    {
        super.createForm();

        this.treeContainer = new PlainContainer();
        this.createTree();
        this.form.grid.addRow(new NullComponent(), this.treeContainer);

        return this.form.container;
    }

    private void createTree()
    {
        this.treeContainer.clear();
        this.treeContainer.addChild(this.createAnimationTree(this.subject.animation, null));
    }

    private AnimationEditor createAnimationEditor(Animation animation)
    {
        AnimationEditor result;

        if (animation instanceof FramedAnimation) {
            FramedAnimation framedAnimation = (FramedAnimation) animation;
            result = new FramedAnimationEditor(this.resources, framedAnimation);

        }
        if (animation instanceof CompoundAnimation) {
            result = new AnimationEditor(animation)
            {
                @Override
                public void onOk()
                {
                    super.onOk();
                    createTree();
                }
            };

        } else if (animation instanceof FramedAnimation) {
            result = new FramedAnimationEditor(this.resources, (FramedAnimation) animation);

        } else {
            result = new AnimationEditor(animation);
        }

        return result;
    }

    private Stylesheet getStylesheet()
    {
        return Itchy.getGame().getStylesheet();
    }
    
    private AbstractComponent createAnimationTree(final Animation animation, final CompoundAnimation parent)
    {

        PlainContainer line = new PlainContainer();
        line.setFill(true, false);

        GuiButton name = new GuiButton(animation.getName());
        name.setExpansion(1);
        line.addChild(name);

        if ((parent != null) && (parent.children.get(0) != animation)) {
            GuiButton up = new GuiButton(new ImageComponent(getStylesheet().resources.getPose("icon_up")
                .getSurface()));
            up.addStyle("compact");
            up.addActionListener(new ActionListener()
            {
                @Override
                public void action()
                {
                    parent.moveAnimationUp(animation);
                    EditAnimation.this.createTree();
                }
            });
            line.addChild(up);
        }

        if (parent != null) {
            GuiButton delete = new GuiButton(new ImageComponent(getStylesheet().resources.getPose(
                "icon_delete")
                .getSurface()));

            delete.addStyle("compact");
            delete.addActionListener(new ActionListener()
            {
                @Override
                public void action()
                {
                    parent.removeAnimation(animation);
                    createTree();
                }
            });
            line.addChild(delete);
        }

        name.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                createAnimationEditor(animation).show();
            }
        });

        if (animation instanceof CompoundAnimation) {
            final CompoundAnimation ca = (CompoundAnimation) animation;

            PlainContainer result = new PlainContainer();
            result.addChild(line);
            result.setLayout(new VerticalLayout());
            result.addStyle("panel");
            result.setFill(true, false);

            PlainContainer indent = new PlainContainer();
            indent.setFill(true, false);
            indent.setLayout(new VerticalLayout());
            indent.addStyle("animationIndent");
            result.addChild(indent);

            for (Animation child : ca.children) {
                indent.addChild(this.createAnimationTree(child, ca));
            }

            GuiButton add = new GuiButton("Add");
            add.addActionListener(new ActionListener()
            {
                @Override
                public void action()
                {
                    addAnimation(ca);
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

    private void addAnimation(final CompoundAnimation ca)
    {
        AnimationTypePicker picker = new AnimationTypePicker()
        {
            @Override
            public void pick(Animation animation)
            {
                Animation child = animation.copy();
                ca.addAnimation(child);
                createTree();
                createAnimationEditor(child).show();
            }
        };
        picker.show();
    }

}
