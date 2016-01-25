package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.Sprite;
import uk.co.nickthecoder.itchy.SpriteSheet;
import uk.co.nickthecoder.itchy.gui.ClickableContainer;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ComponentChangeListener;
import uk.co.nickthecoder.itchy.gui.HorizontalLayout;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Notebook;
import uk.co.nickthecoder.itchy.gui.PlainContainer;
import uk.co.nickthecoder.itchy.gui.Scroll;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class EditSprite extends EditSubject<Sprite>
{
    private SpriteSheet spriteSheet;

    public EditSprite(Resources resources, ListSubjects<Sprite> listSubjects, SpriteSheet spriteSheet, Sprite subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
        this.spriteSheet = spriteSheet;
    }

    @Override
    protected String getSubjectName()
    {
        return "Sprite";
    }

    @Override
    protected Sprite getSubjectByName(String name)
    {
        PoseResource poseResource = this.resources.getPoseResource(name);
        if ( poseResource == null) {
            return null;
        }
        
        if ( poseResource instanceof Sprite) {
            return (Sprite) poseResource;
        } 
        throw new RuntimeException( "Pose exists, but is not a Sprite" );
    }

    @Override
    protected void add()
    {
        this.spriteSheet.addSprite(subject);
        this.resources.addPose(subject);
    }

    @Override
    protected void rename()
    {
        this.resources.renamePose( subject );
    }


    protected Component createForm()
    {
        super.createForm();
        
        Notebook notebook = new Notebook();

        final PlainContainer previewPage = new PlainContainer();
        final ClickableContainer previewClick = new ClickableContainer()
        {
            @Override
            public void onClick(MouseButtonEvent e)
            {
                ((IntegerBox) form.getComponent("offsetX")).setValue(e.x);
                ((IntegerBox) form.getComponent("offsetY")).setValue(e.y);
            }
        };
        final ImageComponent previewImg = new ImageComponent(subject.pose.getSurface());
        previewImg.addStyle("checkered");
        previewClick.addChild(previewImg);

        previewPage.addChild(createScrolledImage(previewClick));
        notebook.addPage("Preview", previewPage);

        PlainContainer spriteSheetPage = new PlainContainer();
        ClickableContainer spriteSheetClick = new ClickableContainer()
        {
            @Override
            public void onClick(MouseButtonEvent e)
            {
                ((IntegerBox) form.getComponent("x")).setValue(e.x);
                ((IntegerBox) form.getComponent("y")).setValue(e.y);
            }
        };
        ImageComponent spriteSheetImg = new ImageComponent(spriteSheet.getSurface());
        spriteSheetImg.addStyle("checkered");
        spriteSheetClick.addChild(spriteSheetImg);

        spriteSheetPage.addChild(createScrolledImage(spriteSheetClick));
        notebook.addPage("Sprite Sheet", spriteSheetPage);

        // Whenever the x,y,width or height change, update the preview image.
        for (String key : new String[] { "x", "y", "width", "height" }) {
            ((IntegerBox) this.form.getComponent(key)).addChangeListener(new ComponentChangeListener()
            {
                @Override
                public void changed()
                {
                    previewImg.setImage(subject.pose.getSurface());
                    previewClick.getParent().getParent().invalidate();
                    previewClick.getParent().getParent().forceLayout();
                }
            });
        }
                
        PlainContainer across = new PlainContainer();
        across.setLayout(new HorizontalLayout());
        across.addChild(form.container);
        across.addChild(notebook);
        return across;
    }

    private Component createScrolledImage(ClickableContainer imageContainer)
    {
        imageContainer.setMinimumWidth(300);
        imageContainer.setMinimumHeight(300);
        
        Scroll scroll = new Scroll(imageContainer);
        scroll.setNaturalHeight(301);
        scroll.setNaturalWidth(301);
        return scroll;
    }
}
