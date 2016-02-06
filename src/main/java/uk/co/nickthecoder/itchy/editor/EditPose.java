package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.FilePoseResource;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.gui.ClickableContainer;
import uk.co.nickthecoder.itchy.gui.Component;
import uk.co.nickthecoder.itchy.gui.ImageComponent;
import uk.co.nickthecoder.itchy.gui.IntegerBox;
import uk.co.nickthecoder.itchy.gui.Label;
import uk.co.nickthecoder.jame.event.MouseButtonEvent;

public class EditPose extends EditNamedSubject<FilePoseResource>
{

    public EditPose(Resources resources, ListSubjects<FilePoseResource> listSubjects, FilePoseResource subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected FilePoseResource getSubjectByName(String name)
    {
        PoseResource result = resources.getPoseResource(name);
        if (result == null) {
            return null;
        }
        if (result instanceof FilePoseResource) {
            return (FilePoseResource) result;
        }
        throw new RuntimeException( "Found a PoseResource, but not of type FilePoseResource" );
    }

    @Override
    protected void rename()
    {
        this.resources.renamePose(subject);
    }

    @Override
    protected String getSubjectName()
    {
        return "Pose";
    }

    @Override
    protected void add()
    {
        this.resources.addPose(subject);
    }

    @Override
    protected Component createForm()
    {
        super.createForm();
        
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

        String size = "(" +
            subject.pose.getSurface().getWidth() + "," +
            subject.pose.getSurface().getHeight() + ")";
        
        form.grid.addRow("Size", new Label( size ) );
        form.grid.addRow("Preview",  previewClick );
        
        return form.container;
    }
}
