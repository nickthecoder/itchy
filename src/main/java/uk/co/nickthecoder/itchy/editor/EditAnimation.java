package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.AnimationResource;
import uk.co.nickthecoder.itchy.Itchy;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.animation.Animation;
import uk.co.nickthecoder.itchy.animation.CompoundAnimation;
import uk.co.nickthecoder.itchy.animation.FramedAnimation;
import uk.co.nickthecoder.itchy.gui.ActionListener;
import uk.co.nickthecoder.itchy.gui.AnimationTypePicker;
import uk.co.nickthecoder.itchy.gui.Button;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.Container;
import uk.co.nickthecoder.itchy.gui.DragTarget;
import uk.co.nickthecoder.itchy.gui.DragableContainer;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.NullComponent;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.RootContainer;
import uk.co.nickthecoder.itchy.gui.Stylesheet;
import uk.co.nickthecoder.itchy.gui.VerticalLayout;
import uk.co.nickthecoder.itchy.gui.VerticalScroll;
import uk.co.nickthecoder.jame.Rect;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;
import uk.co.nickthecoder.jame.event.MouseEvent;

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

        System.out.println( "Created form" );

        return this.form.container;
    }

    private void createTree()
    {
        this.treeContainer.clear();
        this.treeContainer.addChild(this.createAnimationTree(null, this.subject.animation ));
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

    private Component createAnimationTree(final CompoundAnimation parent, final Animation animation )
    {
        System.out.println( "Creating animation tree " + parent  + " child " + animation );
        
        PlainContainer line = new PlainContainer();
        line.setFill(true, false);
        line.addStyle("combo");
        line.setFill( true, true );

        Container result;
        if (animation instanceof CompoundAnimation) {
            result = new PlainContainer();
        } else {
            result = line;
        }

        if (parent != null) {
            DragHandle dragHandle = new DragHandle( result, parent, animation );
            dragHandle.setFill(true, true);
            line.addChild( dragHandle );
        }
        
        Button name = new Button(animation.getName());
        name.setExpansion(1);
        name.addActionListener(new ActionListener()
        {
            @Override
            public void action()
            {
                createAnimationEditor(animation).show();
            }
        });

        line.addChild(name);

        if (parent != null) {
            Button delete = new Button(
                new ImageComponent(getStylesheet().resources.getPose("icon_delete").getSurface()));
            
            delete.addActionListener(new ActionListener() {
                @Override
                public void action()
                {
                    parent.removeAnimation(animation);
                    createTree();
                }
            });
            line.addChild(delete);
        }
        

        if (animation instanceof CompoundAnimation) {
            final CompoundAnimation ca = (CompoundAnimation) animation;

            result.addChild(line);
            result.setLayout(new VerticalLayout());
            result.addStyle("panel");
            result.setFill(true, false);

            PlainContainer indent = new PlainContainer();
            indent.setFill(true, false);
            indent.setLayout(new VerticalLayout());
            indent.addStyle("animationIndent");
            result.addChild(indent);

            AnimationDragTarget target = new AnimationDragTarget( ca, 0 );
            indent.addChild( target );
            
            int count = 0;
            for (Animation child : ca.children) {
                count ++;
                indent.addChild(this.createAnimationTree(ca, child));
                indent.addChild( new AnimationDragTarget( ca, count ) );
            }

            Button add = new Button("Add");
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
                vs.setNaturalHeight(500);
                result.addStyle("animationTree");
                return vs;
            }

        }

        System.out.println( "Created tree" );
        return result;
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

    
    class DragHandle extends DragableContainer
    {
        public Animation animation;
        
        private CompoundAnimation parentAnimation ;

        private Component component;

        private RootContainer root;
        
        private int ox;
        
        private int oy;
        
        private DragTarget dragTarget;
        
        public DragHandle( Component component, CompoundAnimation parentAnimation, Animation animation )
        {
            this.component = component;
            this.parentAnimation = parentAnimation;
            this.animation = animation;
            this.setType("vDrag");
        }
        
        @Override
        public boolean beginDrag(MouseButtonEvent event)
        {
            Rect absRect = this.component.getAbsolutePosition();
            this.ox = event.x;
            this.oy = event.y;

            this.root = new RootContainer();

            this.root.show();
            this.root.setPosition(absRect.x, absRect.y, this.component.getWidth(), this.component.getHeight());
            this.component.remove();
            this.root.addChild( this.component );
            treeContainer.addStyle("dragging");
            
            return true;
        }

        @Override
        public void drag(MouseEvent event, int dx, int dy)
        {
            if ( dragTarget != null ) {
                dragTarget.removeStyle( "hover" );
            }
            Rect rect = this.getAbsolutePosition();

            try {
                int tx = event.x - this.ox;
                int ty = event.y - this.oy;
                this.root.moveTo(this.root.getX() + tx, this.root.getY() + ty);

                event.x += rect.x - treeContainer.getRoot().getX();
                event.y += rect.y - treeContainer.getRoot().getY();
    
                this.dragTarget = DragableContainer.findDragTarget( treeContainer.getRoot(), event, this );
                if (this.dragTarget != null) {
                    System.out.println( "Found drag target to highlight" );
                    this.dragTarget.addStyle( "hover" );
                }
            } finally {
                event.x -= rect.x - treeContainer.getRoot().getX();
                event.y -= rect.y - treeContainer.getRoot().getY();
            }
        }
        
        @Override
        public void endDrag( MouseButtonEvent event, int dx, int dy )
        {
            treeContainer.removeStyle("dragging");
            this.root.hide();
            Rect rect = this.getAbsolutePosition();

            try {

                event.x += rect.x - treeContainer.getRoot().getX();
                event.y += rect.y - treeContainer.getRoot().getY();
             
                DragTarget target = DragableContainer.findDragTarget( treeContainer.getRoot(), event, this );
                System.out.println( "Drag Target : " + target );
                if (target != null) {
                    target.complete( this );
                }
    
                EditAnimation.this.createTree();
            } finally {
                event.x -= rect.x - treeContainer.getRoot().getX();
                event.y -= rect.y - treeContainer.getRoot().getY();
            }
        }
        
    }

    
    class AnimationDragTarget extends PlainContainer implements DragTarget
    {
        private CompoundAnimation compoundAnimation;
        
        private int index;
        
        public AnimationDragTarget( CompoundAnimation compoundAnimtion, int index )
        {
            super();
            this.setType("hDragTarget");
            this.compoundAnimation = compoundAnimtion;
            this.index = index;
        }

        @Override
        public boolean accept(Object source)
        {
            System.out.println( "Accept this drag? " + (source instanceof DragHandle) );
            return source instanceof DragHandle;
        }

        @Override
        public void complete(Object source)
        {
            System.out.println( "Animation dragged to target. Yeah!");
            if (source instanceof DragHandle) {
                DragHandle dragHandle = (DragHandle) source;

                // Add before remove, because if we are moving within the same CompoundAnimation, this
                // will ensure the index is correct. Animations can be a child to more than one parent.
                compoundAnimation.children.add(index,dragHandle.animation);
                dragHandle.parentAnimation.removeAnimation(dragHandle.animation);
            }
        }
    }
}
