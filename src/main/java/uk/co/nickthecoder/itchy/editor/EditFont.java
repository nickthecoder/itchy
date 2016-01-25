package uk.co.nickthecoder.itchy.editor;

import uk.co.nickthecoder.itchy.Font;
import uk.co.nickthecoder.itchy.Resources;

public class EditFont extends EditNamedSubject<Font>
{

    public EditFont(Resources resources, ListSubjects<Font> listSubjects, Font subject, boolean isNew)
    {
        super(resources, listSubjects, subject, isNew);
    }

    @Override
    protected String getSubjectName()
    {
        return "Font";
    }

    @Override
    protected Font getSubjectByName(String name)
    {
        return resources.getFont(name);

    }

    @Override
    protected void add()
    {
        resources.addFont(subject);
    }
    
    @Override
    protected void rename()
    {
        resources.renameFont(subject);
    }

}
