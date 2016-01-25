package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Resources;
import uk.co.nickthecoder.itchy.SoundResource;

public class EditSound extends EditSubject<SoundResource>
{

    public EditSound(Resources resources, ListSubjects<SoundResource> listSubjects, SoundResource subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Sound";
    }

    @Override
    protected SoundResource getSubjectByName(String name)
    {
        return this.resources.getSound(name);
    }

    @Override
    protected void add()
    {
        resources.addSound(subject);        
    }

    @Override
    protected void rename()
    {
        resources.renameSound(subject);        
    }

}
