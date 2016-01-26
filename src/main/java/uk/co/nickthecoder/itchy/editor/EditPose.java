package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.FilePoseResource;
import uk.co.nickthecoder.itchy.PoseResource;
import uk.co.nickthecoder.itchy.Resources;

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
        System.out.println( "Renaming pose : " + subject );
        this.resources.renamePose(subject);
        System.out.println( "Renamed pose : " + resources.getPose(subject.getName() ));
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

}
